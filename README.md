# Cordova Log Remote Export Plugin

Cordova plugin for exporting logcat content and sending logs to a specified server.

## Installation

To install the Cordova Log Remote Export plugin, use the following command in your Cordova project:

```bash
cordova plugin add cordova-plugin-log-remote-export
```

## Usage

### Start Logging

```javascript
LogRemoteExport.start(url, frequencyInMinutes, authorizationHeader, successCallback, errorCallback);
```

- `url`: The URL where log data should be sent.
- `frequencyInMinutes`: The frequency of log exports in minutes.
- `authorizationHeader`: The Authorization header for server authentication.
- `successCallback`: Callback function called on success.
- `errorCallback`: Callback function called on error.

### Stop Logging

```javascript
LogRemoteExport.stop(successCallback, errorCallback);
```

- `successCallback`: Callback function called on success.
- `errorCallback`: Callback function called on error.


## Example

```javascript
// Start logging
LogRemoteExport.start('https://example.com/logs', 2, 'Bearer yourAuthToken', 
    function(successMessage) {
        console.log(successMessage);
    },
    function(errorMessage) {
        console.error(errorMessage);
    }
);

// Stop logging
LogRemoteExport.stop(
    function(successMessage) {
        console.log(successMessage);
    },
    function(errorMessage) {
        console.error(errorMessage);
    }
);
```

## License

This project is licensed under the Apache License - see the LICENSE file for details.