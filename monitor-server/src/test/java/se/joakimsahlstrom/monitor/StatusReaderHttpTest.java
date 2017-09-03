package se.joakimsahlstrom.monitor;

import org.junit.Ignore;
import org.junit.Test;
import se.joakimsahlstrom.monitor.model.Status;

import java.net.URL;

import static org.junit.Assert.*;

public class StatusReaderHttpTest {
    private final StatusReader statusReader = new StatusReaderHttp();

    @Test
    @Ignore
    public void canTestUrl() throws Exception {
        assertEquals(Status.OK, statusReader.getStatus(new URL("http://www.google.com")));
        assertEquals(Status.FAIL, statusReader.getStatus(new URL("http://www.this_is_not_a_real_address_329649238476298472389.com")));
    }

}