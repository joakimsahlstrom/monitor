package se.joakimsahlstrom.monitor;

import rx.Single;
import se.joakimsahlstrom.monitor.model.Status;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class StatusReaderInMemory implements StatusReader {

    private Map<String, Status> statuses = new HashMap<>();

    @Override
    public Single<Status> getStatus(URL url) {
        return Single.just(statuses.getOrDefault(url.toString(), Status.FAIL));
    }

    public void setStatus(String url, Status status) {
        statuses.put(url, status);
    }
}
