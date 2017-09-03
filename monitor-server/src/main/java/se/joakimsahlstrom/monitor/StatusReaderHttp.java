package se.joakimsahlstrom.monitor;

import rx.Single;
import se.joakimsahlstrom.monitor.model.Status;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

// Could probably be done with vertx
public class StatusReaderHttp implements StatusReader {
    @Override
    public Single<Status> getStatus(URL url) {
        return Single.create(s -> {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); // GET is default req. method
                Status status = urlConnection.getResponseCode() == 200 ? Status.OK : Status.FAIL;
                urlConnection.disconnect();
                s.onSuccess(status);
            } catch (IOException e) {
                s.onSuccess(Status.FAIL);
            }
        });
    }
}
