package se.joakimsahlstrom.monitor;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import se.joakimsahlstrom.monitor.model.Service;
import se.joakimsahlstrom.monitor.model.ServiceId;
import se.joakimsahlstrom.monitor.model.ServiceName;
import se.joakimsahlstrom.monitor.model.Status;
import se.joakimsahlstrom.monitor.persistence.MonitorRepositoryVertxFile;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static se.joakimsahlstrom.monitor.MonitorVerticle.ServiceView.DATE_TIME_FORMATTER;

@RunWith(VertxUnitRunner.class)
public class MonitorVerticleTest {
    private static final String DB_FILENAME = MonitorVerticleTest.class.getName() + "_testdata.json";

    private Vertx vertx;

    // For some reason VertxUnitRunner attempts to run my protected non-test methods (getAllServices, add, remove) as
    // tests and I this stops us from letting MonitorVerticleTest directly extend MonitorContract
    // (see: MonitorServiceImplTest). Instead we have to use this kind of ugly hack. Won't dive in to vertx test
    // internals to fix this so it'll have to stay
    private MonitorContractHttp contract;

    public static class MonitorVerticleTestsetup extends MonitorVerticle {
        // would love to remove this hack but since i know nothing of vertx DI i'll settle for this... :-/
        private static final AtomicReference<MonitorService> monitorServiceReference = new AtomicReference<>();
        private static final AtomicReference<StatusReaderInMemory> statusReaderReference = new AtomicReference<>();

        public void start() {
            StatusReaderInMemory statusReader = new StatusReaderInMemory();
            MonitorServiceImpl monitorService = new MonitorServiceImpl(new MonitorRepositoryVertxFile(DB_FILENAME, vertx.fileSystem()), statusReader);
            monitorServiceReference.set(monitorService);
            statusReaderReference.set(statusReader);

            super.start(monitorService, 8082);
        }
    }

    @Before
    public void setUp(TestContext context) {
        File file = new File(DB_FILENAME);
        if (file.exists()) {
            file.delete();
        }

        vertx = Vertx.vertx();
        vertx.deployVerticle(MonitorVerticleTestsetup.class.getName(), context.asyncAssertSuccess());
        contract = new MonitorContractHttp(vertx, MonitorVerticleTestsetup.monitorServiceReference, MonitorVerticleTestsetup.statusReaderReference);
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void canAddAndRemoveServices() throws Exception {
        contract.canAddAndRemoveServices();
    }

    @Test
    public void sameNameAndUrlCanExistMultipleTimes() throws Exception {
        contract.sameNameAndUrlCanExistMultipleTimes();
    }

    @Test
    public void statusUpdatesAreSeenInResult() throws Exception {
        contract.statusUpdatesAreSeenInResult();
    }

    @Test
    public void canRunStatusCheckWithoutData() throws Exception {
        contract.canRunStatusCheckWithoutData();
    }

    static class MonitorContractHttp extends MonitorContract {
        private Vertx vertx;
        private AtomicReference<MonitorService> monitorServiceReference;
        private AtomicReference<StatusReaderInMemory> statusReaderReference;

        public MonitorContractHttp(Vertx vertx, AtomicReference<MonitorService> monitorServiceReference, AtomicReference<StatusReaderInMemory> statusReaderReference) {
            this.vertx = vertx;
            this.monitorServiceReference = monitorServiceReference;
            this.statusReaderReference = statusReaderReference;
        }

        // These helper methods are all blocking while the underlying http client is not.
        // Could definitely be prettier but this did the job well enough

        @Override
        protected Set<Service> getAllServices() throws Exception {
            CountDownLatch answerLatch = new CountDownLatch(1);
            AtomicReference<JsonObject> result = new AtomicReference<>();

            WebClient.create(vertx).get(8082, "localhost", "/service")
                    .send(ar -> {
                        result.set(ar.result().bodyAsJsonObject());
                        answerLatch.countDown();
                    });
            answerLatch.await(1, TimeUnit.SECONDS);
            assertFalse(answerLatch.getCount() > 0);

            return result.get().getJsonArray("services").stream()
                    .map(JsonObject.class::cast)
                    .map(this::parseService)
                    .collect(Collectors.toSet());
        }

        @Override
        protected ServiceId add(String serviceName, String url) throws Exception {
            CountDownLatch answerLatch = new CountDownLatch(1);
            AtomicReference<JsonObject> result = new AtomicReference<>();

            MultiMap formData = MultiMap.caseInsensitiveMultiMap().add("name", serviceName).add("url", url);
            WebClient.create(vertx).post(8082, "localhost", "/service")
                    .sendForm(formData,
                            ar -> {
                                result.set(ar.result().bodyAsJsonObject());
                                answerLatch.countDown();
                            });
            answerLatch.await(1, TimeUnit.SECONDS);
            assertFalse(answerLatch.getCount() > 0);

            return ServiceId.valueOf(result.get().getString("id"));
        }

        @Override
        protected void remove(String serviceId) throws Exception {
            CountDownLatch answerLatch = new CountDownLatch(1);

            WebClient.create(vertx).delete(8082, "localhost", "/service/" + serviceId)
                    .send(ar -> answerLatch.countDown());
            answerLatch.await(1, TimeUnit.SECONDS);
            assertFalse(answerLatch.getCount() > 0);
        }

        @Override
        protected void setStatusReaderStatus(String url, Status status) {
            statusReaderReference.get().setStatus(url, status);
        }

        @Override
        protected void triggerStatusesCheck() {
            monitorServiceReference.get().updateAllStatuses();
        }

        private Service parseService(JsonObject entries) {
            try {
                return new Service(
                        ServiceId.valueOf(entries.getString("id")),
                        ServiceName.valueOf(entries.getString("name")),
                        new URL(entries.getString("url")),
                        Status.valueOf(entries.getString("status")),
                        parseLocalDate(entries));
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Bad url!");
            }
        }

        private LocalDateTime parseLocalDate(JsonObject entries) {
            String lastCheck = entries.getString("lastCheck");
            if (lastCheck.equals(DATE_TIME_FORMATTER.format(LocalDateTime.MIN))) {
                return LocalDateTime.MIN; // cannot be parse():d
            }
            return LocalDateTime.parse(lastCheck, DATE_TIME_FORMATTER);
        }
    }
}
