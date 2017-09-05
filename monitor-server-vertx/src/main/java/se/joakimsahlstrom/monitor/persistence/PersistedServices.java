package se.joakimsahlstrom.monitor.persistence;

import java.util.List;

// Data model for file storage
public class PersistedServices {
    private List<PersistedService> services;

    public PersistedServices() {
        // for json mapper
    }

    public PersistedServices(List<PersistedService> services) {
        this.services = services;
    }

    public void setServices(List<PersistedService> services) {
        this.services = services;
    }

    public List<PersistedService> getServices() {
        return services;
    }
}
