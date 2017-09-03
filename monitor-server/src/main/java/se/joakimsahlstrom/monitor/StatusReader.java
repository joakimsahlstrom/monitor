package se.joakimsahlstrom.monitor;

import rx.Single;
import se.joakimsahlstrom.monitor.model.Status;

import java.net.URL;

public interface StatusReader {
    Single<Status> getStatus(URL url);
}
