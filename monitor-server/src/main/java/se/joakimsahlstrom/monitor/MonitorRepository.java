package se.joakimsahlstrom.monitor;

import se.joakimsahlstrom.monitor.model.Service;
import se.joakimsahlstrom.monitor.model.ServiceId;

import java.util.Collection;

public interface MonitorRepository {
    Collection<Service> readAllServices();
    void createOrUpdateService(Service service);
    void delete(ServiceId id);

    Service get(ServiceId serviceId);
}
