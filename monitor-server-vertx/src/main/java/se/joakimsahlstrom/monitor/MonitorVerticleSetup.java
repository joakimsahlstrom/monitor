package se.joakimsahlstrom.monitor;

import se.joakimsahlstrom.monitor.persistence.MonitorRepositoryVertxFile;

public class MonitorVerticleSetup extends MonitorVerticle {

    public void start() {
        MonitorService monitorService = new MonitorServiceImpl(MonitorRepositoryVertxFile.create("monitors.json", vertx.fileSystem()), new StatusReaderHttp());
        super.start(monitorService, config().getInteger("http.port", 8080));
    }

}
