package se.joakimsahlstrom.monitor.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Created by joakim on 2017-09-02.
 */
public class ServiceId {

    private String id;

    private ServiceId(String id) {
        this.id = Objects.requireNonNull(id);
    }

    public static ServiceId valueOf(String id) {
        return new ServiceId(id);
    }

    public static ServiceId createNew() {
        return new ServiceId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceId serviceId = (ServiceId) o;

        return id != null ? id.equals(serviceId.id) : serviceId.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
