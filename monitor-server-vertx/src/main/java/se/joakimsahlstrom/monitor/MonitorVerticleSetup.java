package se.joakimsahlstrom.monitor;

import se.joakimsahlstrom.monitor.persistence.MonitorRepositoryVertxFile;

import java.util.concurrent.TimeUnit;

public class MonitorVerticleSetup extends MonitorVerticle {

    public void start() {
        // DI
        MonitorService monitorService = new MonitorServiceImpl(MonitorRepositoryVertxFile.create("monitors.json", vertx.fileSystem()), new StatusReaderHttp());

        super.start(monitorService, config().getInteger("server.port", 8081)); // where are config settings read from? Oh well, maybe you appreciate the effort :D
        super.startBackground(monitorService, config().getLong("status.refresh", TimeUnit.SECONDS.toMillis(60)));
    }

}
