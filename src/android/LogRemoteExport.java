package logremoteexport.cordova;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

public class LogRemoteExport extends CordovaPlugin {

    private Timer logExportTimer;
    private String exportUrl;
    private String authorizationHeader;

    private static final String TAG = "LogRemoteExport";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("startLogging")) {
            try {
                if (logExportTimer == null) {
                    String url = args.getString(0);
                    int frequencyInMinutes = args.getInt(1);
                    String authHeader = null;

                    if (args.length() > 2 && !args.isNull(2)) {
                        authHeader = args.getString(2);
                    }

                    startLogging(url, frequencyInMinutes, authHeader);
                    callbackContext.success("Logging started successfully.");
                    Log.d(TAG, "Logging started.");
                } else {
                    callbackContext.error("Logging is already in progress.");
                    Log.w(TAG, "Logging is already in progress.");
                }
                return true;
            } catch (Exception e) {
                callbackContext.error("Error starting logging: " + e.getMessage());
                Log.e(TAG, "Error starting logging: " + e.getMessage());
                return false;
            }
        } else if (action.equals("stopLogging")) {
            stopLogging();
            callbackContext.success("Logging stopped.");
            return true;
        }
        return false;
    }

    private void startLogging(String url, int frequencyInMinutes, String authHeader) {
        this.exportUrl = url;
        this.authorizationHeader = authHeader;

        logExportTimer = new Timer();
        logExportTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                exportLogcatContent();
            }
        }, 0, frequencyInMinutes * 60 * 1000); // Export logcat content based on the provided frequency
    }

    private void stopLogging() {
        if (logExportTimer != null) {
            logExportTimer.cancel();
            logExportTimer.purge();
            logExportTimer = null;
        }
    }

    private void exportLogcatContent() {
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                log.append(line).append("\n");
            }

            sendLogToServer(log.toString());
            Log.d(TAG, "Logcat content exported successfully.");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error exporting logcat content: " + e.getMessage());
        }
    }

    private void sendLogToServer(final String logContent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(exportUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Authorization", authorizationHeader);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // Construct a JSON object with a single index "log"
                    JSONObject jsonLog = new JSONObject();
                    jsonLog.put("log", logContent);

                    // Convert the JSON object to bytes and send to the server
                    byte[] postData = jsonLog.toString().getBytes();
                    connection.getOutputStream().write(postData);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Log sent successfully
                        // You can add further handling or logging if needed
                        Log.d(TAG, "Log sent to server successfully.");
                    } else {
                        // Log sending failed
                        // You can add further handling or logging if needed
                        Log.e(TAG, "Error sending log to server. Response code: " + responseCode);
                    }

                    connection.disconnect();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error sending log to server: " + e.getMessage());
                }
            }
        }).start();
    }

}