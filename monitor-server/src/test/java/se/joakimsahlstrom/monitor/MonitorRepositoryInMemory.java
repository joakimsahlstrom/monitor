package se.joakimsahlstrom.monitor;

import se.joakimsahlstrom.monitor.model.Service;
import se.joakimsahlstrom.monitor.model.ServiceId;

import java.util.*;

public class MonitorRepositoryInMemory implements MonitorRepository {

    private Map<ServiceId, Service> serviceMap = Collections.synchronizedMap(new HashMap<>());

    @Override
    public Collection<Service> readAllServices() {
        return serviceMap.values();
    }

    @Override
    public void createOrUpdateService(Service service) {
        synchronized (serviceMap) {
            serviceMap.put(service.getId(), service);
        }
    }

    @Override
    public void delete(ServiceId id) {
        synchronized (serviceMap) {
            serviceMap.remove(id);
        }
    }

    @Override
    public Service get(ServiceId serviceId) {
        return Optional.ofNullable(serviceMap.get(serviceId))
                .orElseThrow(() -> new NoSuchElementException("No service with id=" + serviceId + " found!"));
    }
}
