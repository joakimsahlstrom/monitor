package se.joakimsahlstrom.monitor;

import se.joakimsahlstrom.monitor.model.Status;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class StatusReaderHttp implements StatusReader {
    @Override
    public Status getStatus(URL url) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); // GET is default req. method
            Status status = urlConnection.getResponseCode() == 200 ? Status.OK : Status.FAIL;
            urlConnection.disconnect();
            return status;
        } catch (IOException e) {
            return Status.FAIL;
        }
    };
}
