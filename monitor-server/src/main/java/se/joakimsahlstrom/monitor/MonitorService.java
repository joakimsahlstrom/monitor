package se.joakimsahlstrom.monitor;

import rx.Observable;
import rx.Single;
import se.joakimsahlstrom.monitor.model.Service;
import se.joakimsahlstrom.monitor.model.ServiceId;
import se.joakimsahlstrom.monitor.model.ServiceName;

import java.net.URL;

/**
 * Created by joakim on 2017-09-02.
 *
 * I wanted to maintain an reactive approach, some decision could be challenged, like having
 * getAllServices return Observable<Service> instead of Single<Collection<Service>>
 */
public interface MonitorService {

    Observable<Service> getAllServices();
    Single<ServiceId> add(ServiceName serviceName, URL url);
    Single<Void> remove(ServiceId id);

    void updateAllStatuses();

}
