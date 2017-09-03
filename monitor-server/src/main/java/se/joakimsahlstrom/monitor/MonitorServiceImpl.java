package se.joakimsahlstrom.monitor;

import se.joakimsahlstrom.monitor.model.Service;
import se.joakimsahlstrom.monitor.model.ServiceId;
import se.joakimsahlstrom.monitor.model.ServiceName;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MonitorServiceImpl implements MonitorService {
    private MonitorRepository monitorRepository;
    private StatusReader statusReader;

    public MonitorServiceImpl(MonitorRepository monitorRepository, StatusReader statusReader) {
        this.monitorRepository = Objects.requireNonNull(monitorRepository);
        this.statusReader = Objects.requireNonNull(statusReader);
    }

    @Override
    public Set<Service> getAllServices() {
        return new HashSet<>(monitorRepository.readAllServices());
    }

    @Override
    public ServiceId add(ServiceName serviceName, URL url) {
        // Allow multiple services with same url and name!
        Service createService = Service.createNew(serviceName, url);
        monitorRepository.createOrUpdateService(createService);
        return createService.getId();
    }

    @Override
    public void remove(ServiceId id) {
        monitorRepository.delete(id);
    }

    @Override
    public void updateAllStatuses() {
        monitorRepository.readAllServices().stream()
                .map(Service::getId)
                .forEach(this::updateStatus);
    }

    @Override
    public void updateStatus(ServiceId serviceId) {
        Service service = monitorRepository.get(serviceId);
        monitorRepository.createOrUpdateService(
                service.withNewStatus(statusReader.getStatus(service.getUrl()), LocalDateTime.now()));
    }
}
