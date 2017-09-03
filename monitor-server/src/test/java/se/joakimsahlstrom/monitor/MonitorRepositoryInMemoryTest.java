package se.joakimsahlstrom.monitor;

public class MonitorRepositoryInMemoryTest extends MonitorRepositoryContract {

    private MonitorRepository monitorRepository = new MonitorRepositoryInMemory();

    @Override
    public MonitorRepository getMonitorRepository() {
        return monitorRepository;
    }

}