package se.joakimsahlstrom.monitor;

import rx.Observable;
import rx.Single;
import se.joakimsahlstrom.monitor.model.Service;
import se.joakimsahlstrom.monitor.model.ServiceId;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MonitorRepositoryInMemory implements MonitorRepository {

    private Map<ServiceId, Service> serviceMap = Collections.synchronizedMap(new HashMap<>());

    @Override
    public Observable<Service> readAllServices() {
        return Observable.from(serviceMap.values());
    }

    @Override
    public Single<Void> createOrUpdateService(Service service) {
        return Single.create(singleSubscriber -> {
            synchronized (serviceMap) {
                serviceMap.put(service.getId(), service);
                singleSubscriber.onSuccess(null);
            }
        });
    }

    @Override
    public Single<Void> delete(ServiceId id) {
        return Single.create(singleSubscriber -> {
            synchronized (serviceMap) {
                serviceMap.remove(id);
            }
            singleSubscriber.onSuccess(null);
        });
    }

}
