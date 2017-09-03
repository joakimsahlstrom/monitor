package se.joakimsahlstrom.monitor.model;

import java.net.URL;
import java.time.LocalDateTime;

import static java.util.Objects.requireNonNull;

/**
 * Created by joakim on 2017-09-02.
 */
public class Service {

    private ServiceId id;
    private ServiceName name;
    private URL url;
    private Status status;
    private LocalDateTime lastCheck;

    public Service(ServiceId id, ServiceName name, URL url, Status status, LocalDateTime lastCheck) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        this.url = requireNonNull(url);
        this.status = requireNonNull(status);
        this.lastCheck = lastCheck;
    }

    public static Service createNew(ServiceName serviceName, URL url) {
        return new Service(ServiceId.createNew(), serviceName, url, Status.FAIL, LocalDateTime.MIN);
    }

    public ServiceId getId() {
        return id;
    }

    public ServiceName getName() {
        return name;
    }

    public URL getUrl() {
        return url;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getLastCheck() {
        return lastCheck;
    }

    public Service withNewStatus(Status status, LocalDateTime lastCheck) {
        return new Service(this.id, this.name, this.url, status, lastCheck);
    }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", name=" + name +
                ", url=" + url +
                ", status=" + status +
                ", lastCheck=" + lastCheck +
                '}';
    }
}
