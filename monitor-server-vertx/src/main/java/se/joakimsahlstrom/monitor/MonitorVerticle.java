package se.joakimsahlstrom.monitor;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import se.joakimsahlstrom.monitor.model.Service;
import se.joakimsahlstrom.monitor.model.ServiceId;
import se.joakimsahlstrom.monitor.model.ServiceName;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by joakim on 2017-09-02.
 */
public class MonitorVerticle extends AbstractVerticle {

    static class ServicesView {
        List<ServiceView> services;

        public ServicesView(Collection<Service> services) {
            this.services = Collections.unmodifiableList(services.stream()
                    .map(ServiceView::new)
                    .sorted(Comparator.comparing(ServiceView::getName))
                    .collect(Collectors.toList()));
        }

        public static ServicesView empty() {
            return new ServicesView(Collections.emptyList());
        }

        public List<ServiceView> getServices() {
            return services;
        }
    }

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

    public void start(MonitorService monitor, int port) {
        HttpServer httpServer = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route(HttpMethod.GET, "/service")
                .blockingHandler(toJsonHandler(context -> new ServicesView(monitor.getAllServices())));
        router.post("/service").handler(BodyHandler.create()); // Needed for FORM attribute processing
        router.post("/service")
                .blockingHandler(context -> {
                    ServiceId serviceId = monitor.add(
                            ServiceName.valueOf(context.request().getFormAttribute("name")),
                            toUrl(context.request().getFormAttribute("url")));
                    context.response()
                            .end(Json.encode(new ServiceIdView(serviceId)));
                });
        router.route(HttpMethod.DELETE, "/service/:serviceId")
                .blockingHandler(context -> {
                    monitor.remove(ServiceId.valueOf(context.request().getParam("serviceId")));
                    context.response().end("OK");
                });

        httpServer.requestHandler(router::accept).listen(port);
    }

    private URL toUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Bad url=" + url);
        }
    }

    private Handler<RoutingContext> toJsonHandler(Function<RoutingContext, Object> dataRetriever) {
        return context -> context.response().putHeader("content-type", "application/json").end(Json.encode(dataRetriever.apply(context)));
    }

}
