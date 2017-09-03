package se.joakimsahlstrom.monitor.persistence;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.rxjava.core.file.FileSystem;
import rx.Emitter;
import rx.Observable;
import rx.Single;
import rx.functions.Action1;
import se.joakimsahlstrom.monitor.MonitorRepository;
import se.joakimsahlstrom.monitor.model.Service;
import se.joakimsahlstrom.monitor.model.ServiceId;

import java.util.ArrayList;

// This class is not threa-safe, decided to not spend any time trying (and likely eventually failing) to get that right
public class MonitorRepositoryVertxFile implements MonitorRepository {

    private String filePath;
    private FileSystem fileSystem;

    public MonitorRepositoryVertxFile(String filePath, FileSystem fileSystem) {
        this.filePath = filePath;
        this.fileSystem = fileSystem;
    }

    public static MonitorRepositoryVertxFile create(String filePath, FileSystem fileSystem) {
        return new MonitorRepositoryVertxFile(filePath, fileSystem);
    }

    @Override
    public Observable<Service> readAllServices() {
        return Observable.create((Action1<Emitter<PersistedService>>) serviceEmitter ->
                fileSystem.readFile(filePath, ar -> {
                    if (ar.failed()) {
                        serviceEmitter.onCompleted();
                    } else {
                        PersistedServices persistedServices = Json.decodeValue(ar.result().toString(), PersistedServices.class);
                        persistedServices.getServices().stream()
                                .forEach(service -> serviceEmitter.onNext(service));
                        serviceEmitter.onCompleted();
                    }
                }), Emitter.BackpressureMode.BUFFER)
                .map(PersistedService::toService);
    }

    @Override
    public Single<Void> createOrUpdateService(Service service) {
        return Single.create(subscriber ->
                readAllServices()
                        .filter(s -> !s.getId().equals(service.getId()))
                        .map(PersistedService::create)
                        .collect(() -> new ArrayList<PersistedService>(), (l, s) -> l.add(s))
                        .subscribe(services -> {
                            services.add(PersistedService.create(service));
                            fileSystem.writeFile(
                                    filePath,
                                    toRxBuffer(Json.encodeToBuffer(new PersistedServices(services))),
                                    ar -> subscriber.onSuccess(null));
                        }));
    }

    private io.vertx.rxjava.core.buffer.Buffer toRxBuffer(Buffer buffer) {
        return new io.vertx.rxjava.core.buffer.Buffer(buffer);
    }

    @Override
    public Single<Void> delete(ServiceId id) {
        return Single.create(subsriber ->
                readAllServices()
                        .filter(s -> !s.getId().equals(id))
                        .map(PersistedService::create)
                        .collect(() -> new ArrayList<PersistedService>(), (l, s) -> l.add(s))
                        .subscribe(services ->
                            fileSystem.writeFile(
                                    filePath,
                                    toRxBuffer(Json.encodeToBuffer(new PersistedServices(services))),
                                    ar -> subsriber.onSuccess(null))
                        ));
    }

}
