package se.joakimsahlstrom.monitor;

import rx.Observable;
import rx.Single;
import se.joakimsahlstrom.monitor.model.Service;
import se.joakimsahlstrom.monitor.model.ServiceId;
import se.joakimsahlstrom.monitor.model.ServiceName;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class MonitorServiceImpl implements MonitorService {
    private MonitorRepository monitorRepository;
    private StatusReader statusReader;

    public MonitorServiceImpl(MonitorRepository monitorRepository, StatusReader statusReader) {
        this.monitorRepository = Objects.requireNonNull(monitorRepository);
        this.statusReader = Objects.requireNonNull(statusReader);
    }

    @Override
    public Observable<Service> getAllServices() {
        return monitorRepository.readAllServices();
    }

    @Override
    public Single<ServiceId> add(ServiceName serviceName, URL url) {
        // Allow multiple services with same url and name!
        Service createdService = Service.createNew(serviceName, url);
        return monitorRepository.createOrUpdateService(createdService)
                .map(v -> createdService.getId());
    }

    @Override
    public Single<Void> remove(ServiceId id) {
        return monitorRepository.delete(id);
    }

    @Override
    public void updateAllStatuses() {
        ArrayList<Service> services = monitorRepository.readAllServices()
                .collect(ArrayList<Service>::new, (l, service) -> l.add(service))
                .toBlocking().single();
        services.forEach(s -> updateStatus(s).toBlocking().value());
    }

    private Single<Void> updateStatus(Service service) {
        return statusReader.getStatus(service.getUrl())
                .flatMap(status -> monitorRepository.createOrUpdateService(service.withNewStatus(status, LocalDateTime.now())));
    }
}
