package se.joakimsahlstrom.monitor;

import rx.Observable;
import rx.Single;
import se.joakimsahlstrom.monitor.model.Service;
import se.joakimsahlstrom.monitor.model.ServiceId;

public interface MonitorRepository {
    Observable<Service> readAllServices();
    Single<Void> createOrUpdateService(Service service);
    Single<Void> delete(ServiceId id);
}
