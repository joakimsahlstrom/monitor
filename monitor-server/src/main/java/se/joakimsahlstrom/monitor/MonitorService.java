package se.joakimsahlstrom.monitor;

import se.joakimsahlstrom.monitor.model.Service;
import se.joakimsahlstrom.monitor.model.ServiceId;
import se.joakimsahlstrom.monitor.model.ServiceName;
import se.joakimsahlstrom.monitor.model.Status;

import java.net.URL;
import java.util.Set;

/**
 * Created by joakim on 2017-09-02.
 */
public interface MonitorService {

    Set<Service> getAllServices();
    ServiceId add(ServiceName serviceName, URL url);
    void remove(ServiceId id);

    void updateAllStatuses();
    void updateStatus(ServiceId serviceId);

}
