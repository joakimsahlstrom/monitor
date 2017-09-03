package se.joakimsahlstrom.monitor;

import org.junit.Test;
import se.joakimsahlstrom.monitor.model.Service;
import se.joakimsahlstrom.monitor.model.ServiceId;
import se.joakimsahlstrom.monitor.model.Status;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class MonitorContract {

    @Test
    public void canAddAndRemoveServices() throws Exception {
        assertTrue(getAllServices().isEmpty());

        ServiceId service1 = add("service1", "http://www.service1.com");
        assertContents(getAllServices(), service1);

        ServiceId service2 = add("service2", "http://www.service2.com");
        assertContents(getAllServices(), service1, service2);

        remove(service1.toString());
        assertContents(getAllServices(), service2);
    }

    @Test
    public void sameNameAndUrlCanExistMultipleTimes() throws Exception {
        ServiceId service1 = add("service1", "http://www.service1.com");
        ServiceId service2 = add("service1", "http://www.service1.com");

        assertContents(getAllServices(), service1, service2);
    }

    @Test
    public void statusUpdatesAreSeenInResult() throws Exception {
        ServiceId service1 = add("service1", "http://www.service1.com");
        ServiceId service2 = add("service2", "http://www.service2.com");

        assertStatus(service1, Status.FAIL);
        assertStatus(service2, Status.FAIL);

        setStatusReaderStatus("http://www.service1.com", Status.OK);
        setStatusReaderStatus("http://www.service2.com", Status.FAIL);

        triggerStatusesCheck();

        assertStatus(service1, Status.OK);
        assertStatus(service2, Status.FAIL);
    }

    @Test
    public void canRunStatusCheckWithoutData() throws Exception {
        triggerStatusesCheck();
    }

    // Helper methods

    protected void assertStatus(ServiceId serviceId, Status ok) throws Exception {
        assertEquals("Unexpected status for serviceId=" + serviceId, ok, getService(serviceId).getStatus());
    }

    private Service getService(ServiceId serviceId) throws Exception {
        return getAllServices().stream().filter(s -> s.getId().equals(serviceId)).findAny().get();
    }

    protected static void assertContents(Set<Service> allServices, ServiceId... serviceIds) {
        assertEquals(allServices.size(), serviceIds.length);
        Stream.of(serviceIds).forEach(serviceId -> assertContains(allServices, serviceId));
    }

    private static void assertContains(Set<Service> allServices, ServiceId serviceId) {
        assertTrue("Expected service with id=" + serviceId + " but there was none among "
                        + allServices.stream().map(Service::getId).collect(Collectors.toList()),
                allServices.stream().filter(s -> s.getId().equals(serviceId)).findAny().isPresent());
    }

    // Used by contract tests for settings up and verifying test cases, implement these!

    protected abstract ServiceId add(String serviceName, String url) throws Exception;
    protected abstract void remove(String serviceId) throws Exception;
    protected abstract void setStatusReaderStatus(String url, Status status);
    protected abstract void triggerStatusesCheck();

    protected abstract Set<Service> getAllServices() throws Exception;
}
