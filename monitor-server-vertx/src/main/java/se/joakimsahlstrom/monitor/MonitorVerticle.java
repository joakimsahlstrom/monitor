package se.joakimsahlstrom.monitor;

import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import se.joakimsahlstrom.monitor.model.Service;
import se.joakimsahlstrom.monitor.model.ServiceId;
import se.joakimsahlstrom.monitor.model.ServiceName;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Created by joakim on 2017-09-02.
 *
 * A subclass of this class is required for actual verticle startup as no start()-method is implemented here
 * @see MonitorVerticleSetup
 */
public class MonitorVerticle extends AbstractVerticle {

    // Model corresponding to task spec
    static class ServiceView {
        public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        private Service service;

        public ServiceView(Service service) {
            this.service = Objects.requireNonNull(service);
        }

        public String getId() {
            return service.getId().toString();
        }

        public String getName() {
            return service.getName().toString();
        }

        public String getUrl() {
            return service.getUrl().toExternalForm();
        }

        public String getStatus() {
            return service.getStatus().name();
        }

        public String getLastCheck() {
            return service.getLastCheck().format(DATE_TIME_FORMATTER);
        }
    }

    static class ServiceIdView {
        private ServiceId serviceId;

        public ServiceIdView(ServiceId serviceId) {
            this.serviceId = serviceId;
        }

        public String getId() {
            return serviceId.toString();
        }
    }

    // Setup server and request routing
    // NOTE: I did not take the time to do proper error handling
    public void start(MonitorService monitor, int port) {
        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.get("/service")
                .produces("application/json")
                .handler(getHandler(monitor));
        router.post("/service").handler(BodyHandler.create()); // Needed for FORM attribute processing
        router.post("/service").handler(postHandler(monitor));
        router.delete("/service/:serviceId").handler(deleteHandler(monitor));

        httpServer.requestHandler(router::accept)
                .listen(port);
    }

    // Setup scheduled jobs
    public void startBackground(MonitorService monitorService, long delay) {
        // Using periodic here, thus assuming refresh takes shorter time than delay
        vertx.setPeriodic(delay, e -> {
            vertx.executeBlocking(f -> monitorService.updateAllStatuses(), ar -> {});
        });
    }

    // Request handler setups

    private Handler<RoutingContext> getHandler(MonitorService monitor) {
        return context -> {
            // We stream the entire response here, a bit unnecessary but i wanted to try it
            HttpServerResponse response = context.response()
                    .setChunked(true)
                    .write("{ \"services\": [");
            Delimiter delimiter = new Delimiter();
            monitor.getAllServices()
                    .map(ServiceView::new)
                    .map(Json::encode)
                    .doOnCompleted(() -> response.end("] }"))
                    .subscribe(serviceViewJson -> response.write(delimiter.get() + serviceViewJson));
        };
    }

    private Handler<RoutingContext> postHandler(MonitorService monitor) {
        // lets respond with newly created service id, nice to have in testing etc
        return context ->
                monitor.add(getServiceName(context), getUrl(context))
                        .map(ServiceIdView::new)
                        .map(Json::encode)
                        .subscribe(serviceId -> context.response().end(serviceId));
    }

    private Handler<RoutingContext> deleteHandler(MonitorService monitor) {
        return context ->
                monitor.remove(getServiceId(context))
                        .subscribe(v -> context.response().end("OK"));
    }

    // Helper methods

    private ServiceId getServiceId(RoutingContext context) {
        return ServiceId.valueOf(context.request().getParam("serviceId"));
    }

    private ServiceName getServiceName(RoutingContext context) {
        return ServiceName.valueOf(context.request().getParam("name"));
    }

    private URL getUrl(RoutingContext context) {
        String url = context.request().getParam("url");
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Bad url=" + url);
        }
    }

    // Helper classes

    private class Delimiter {
        boolean first = true;

        String get() {
            if (first) {
                first = false;
                return "";
            } else {
                return ", ";
            }
        }
    }

}
