package se.joakimsahlstrom.monitor;

import se.joakimsahlstrom.monitor.model.Status;

import java.net.URL;

public interface StatusReader {
    Status getStatus(URL url);
}
