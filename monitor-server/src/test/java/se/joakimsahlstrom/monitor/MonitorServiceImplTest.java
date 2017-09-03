package se.joakimsahlstrom.monitor;

import se.joakimsahlstrom.monitor.model.Service;
import se.joakimsahlstrom.monitor.model.ServiceId;
import se.joakimsahlstrom.monitor.model.ServiceName;
import se.joakimsahlstrom.monitor.model.Status;

import java.net.URL;
import java.util.Set;

public class MonitorServiceImplTest extends MonitorContract {
    private final StatusReaderInMemory statusReader = new StatusReaderInMemory();
    private MonitorService monitor = new MonitorServiceImpl(new MonitorRepositoryInMemory(), statusReader);

    @Override
    public Set<Service> getAllServices() {
        return monitor.getAllServices();
    }

    @Override
    public ServiceId add(String serviceName, String url) throws Exception {
        return monitor.add(ServiceName.valueOf(serviceName), new URL(url));
    }

    @Override
    public void remove(String serviceId) {
        monitor.remove(ServiceId.valueOf(serviceId));
    }

    @Override
    protected void setStatusReaderStatus(String url, Status status) {
        statusReader.setStatus(url, status);
    }

    @Override
    protected void triggerStatusesCheck() {
        monitor.updateAllStatuses();
    }


}