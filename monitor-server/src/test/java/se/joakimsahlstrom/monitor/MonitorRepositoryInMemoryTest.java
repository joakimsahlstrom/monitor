package se.joakimsahlstrom.monitor;

import org.junit.Test;
import se.joakimsahlstrom.monitor.model.Service;
import se.joakimsahlstrom.monitor.model.ServiceName;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class MonitorRepositoryInMemoryTest extends MonitorRepositoryContract {

    private MonitorRepository monitorRepository = new MonitorRepositoryInMemory();

    @Override
    public MonitorRepository getMonitorRepository() {
        return monitorRepository;
    }

}