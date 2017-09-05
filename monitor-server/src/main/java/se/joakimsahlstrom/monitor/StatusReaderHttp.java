package se.joakimsahlstrom.monitor;

import rx.Single;
import se.joakimsahlstrom.monitor.model.Status;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

// Could probably be done with vertx
public class StatusReaderHttp implements StatusReader {
    // Maybe we should use the vertx thread pool here but oh well
    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public Single<Status> getStatus(URL url) {
        return Single.create(s ->
                executor.execute(() -> {
                    try {
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); // GET is default req. method
                        Status status = urlConnection.getResponseCode() == 200 ? Status.OK : Status.FAIL;
                        urlConnection.disconnect();
                        s.onSuccess(status);
                    } catch (IOException e) {
                        s.onSuccess(Status.FAIL);
                    }
                })
        );
    }
}
