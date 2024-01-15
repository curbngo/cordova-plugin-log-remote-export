var exec = require("cordova/exec");

var LogRemoteExport = {
    start: function (url, frequencyInMinutes, authorizationHeader, successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'LogRemoteExport', 'startLogging', [url, frequencyInMinutes, authorizationHeader]);
    },
    stop: function (successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'LogRemoteExport', 'stopLogging', []);
    }
};

module.exports = LogRemoteExport;
