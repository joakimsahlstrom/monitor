package se.joakimsahlstrom.monitor.model;

import java.util.Objects;

/**
 * Created by joakim on 2017-09-02.
 */
public class ServiceName {

    private String name;

    private ServiceName(String name) {
        Objects.requireNonNull(name);
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name must be non empty string (whitespaces are trimmed)!");
        }

        this.name = name.trim();
    }

    public static ServiceName valueOf(String name) {
        return new ServiceName(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceName that = (ServiceName) o;

        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
