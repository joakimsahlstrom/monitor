package se.joakimsahlstrom.monitor;

import org.junit.Test;
import se.joakimsahlstrom.monitor.model.Service;
import se.joakimsahlstrom.monitor.model.ServiceName;
import se.joakimsahlstrom.monitor.model.Status;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

public abstract class MonitorRepositoryContract {

    protected abstract MonitorRepository getMonitorRepository();

    @Test
    public void writtenServiceIsReturned() throws Exception {
        Service service1 = Service.createNew(ServiceName.valueOf("service1"), new URL("http://www.service1.com"));
        getMonitorRepository().createOrUpdateService(service1).toBlocking().value();

        assertContentsUnordered(Arrays.asList(service1), readServices());
    }

    @Test
    public void canWriteAndDeleteServices() throws Exception {
        Service service1 = Service.createNew(ServiceName.valueOf("service1"), new URL("http://www.service1.com"));
        Service service2 = Service.createNew(ServiceName.valueOf("service2"), new URL("http://www.service2.com"));

        getMonitorRepository().createOrUpdateService(service1).toBlocking().value();
        getMonitorRepository().createOrUpdateService(service2).toBlocking().value();

        assertContentsUnordered(Arrays.asList(service1, service2), readServices());

        getMonitorRepository().delete(service2.getId()).toBlocking().value();
        assertContentsUnordered(Arrays.asList(service1), readServices());

        getMonitorRepository().delete(service1.getId()).toBlocking().value();
        assertContentsUnordered(Arrays.asList(), readServices());
    }

    @Test
    public void canUpdateService() throws Exception {
        Service service1 = Service.createNew(ServiceName.valueOf("service1"), new URL("http://www.service1.com"));

        getMonitorRepository().createOrUpdateService(service1).toBlocking().value();
        assertContentsUnordered(Arrays.asList(service1), readServices());

        Service updatedService = service1.withNewStatus(Status.OK, LocalDateTime.MIN);
        getMonitorRepository().createOrUpdateService(updatedService).toBlocking().value();
        assertContentsUnordered(Arrays.asList(updatedService), readServices());
    }

    // Helpers

    private ArrayList<Service> readServices() {
        return getMonitorRepository().readAllServices().collect(() -> new ArrayList<Service>(), (l, s) -> l.add(s)).toBlocking().single();
    }

    private static <T> void assertContentsUnordered(List<T> l1, List<T> l2) {
        assertEquals(new HashSet<>(l1), new HashSet<>(l2));
    }
}
