package se.joakimsahlstrom.monitor.persistence;

import se.joakimsahlstrom.monitor.model.Service;
import se.joakimsahlstrom.monitor.model.ServiceId;
import se.joakimsahlstrom.monitor.model.ServiceName;
import se.joakimsahlstrom.monitor.model.Status;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Data model for file storage
public class PersistedService {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private String id;
    private String name;
    private String url;
    private String status;
    private String lastCheck;

    public PersistedService() {
        // for json mapper
    }

    public PersistedService(String id, String name, String url, String status, String lastCheck) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.status = status;
        this.lastCheck = lastCheck;
    }

    public static PersistedService create(Service service) {
        return new PersistedService(service.getId().toString(),
                service.getName().toString(),
                service.getUrl().toString(),
                service.getStatus().toString(),
                service.getLastCheck().format(DATE_TIME_FORMATTER));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getStatus() {
        return status;
    }

    public String getLastCheck() {
        return lastCheck;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLastCheck(String lastCheck) {
        this.lastCheck = lastCheck;
    }

    public Service toService() {
        try {
            return new Service(ServiceId.valueOf(id), ServiceName.valueOf(name), new URL(url), Status.valueOf(status), parseLocalDate(lastCheck));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Bad URL!", e);
        }
    }

    private LocalDateTime parseLocalDate(String lastCheck) {
        if (lastCheck.equals(DATE_TIME_FORMATTER.format(LocalDateTime.MIN))) {
            return LocalDateTime.MIN; // cannot be parse():d
        }
        return LocalDateTime.parse(lastCheck, DATE_TIME_FORMATTER);
    }

}
