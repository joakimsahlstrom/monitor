package se.joakimsahlstrom.monitor.persistence;

import io.vertx.rxjava.core.Vertx;
import org.junit.Before;
import se.joakimsahlstrom.monitor.MonitorRepository;
import se.joakimsahlstrom.monitor.MonitorRepositoryContract;

import java.io.File;

import static org.junit.Assert.*;

public class MonitorRepositoryVertxFileTest extends MonitorRepositoryContract {

    @Before
    public void before() {
        File file = new File("test.json");
        if (file.exists()) {
            file.delete();
        }
    }

    protected MonitorRepository getMonitorRepository() {
        Vertx vertx = Vertx.vertx();
        return new MonitorRepositoryVertxFile("test.json", vertx.fileSystem());
    }

}