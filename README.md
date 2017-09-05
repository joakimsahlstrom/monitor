# monitor-parent

## Module structure
Divided into three parts;

### monitor-server
Domain model and logic

### monitor-server-vertx
vert.x endpoint

### monitor-web
Web server


## Running
Build all using maven (`mvn clean install`), start vertx server with 

`monitor-parent/monitor-server-vertx > java -jar target/monitor-server-vertx-1.0-SNAPSHOT-fat.jar`

port is hard coded to port 8081

monitor-web is a spring-boot app. Run from IntelliJ or 

`monitor-parent/monitor-web > mvn spring-boot:run`

Web server is hard coded to port 8080

GUI should now be available on `http://localhost:8080/services`

Note: endpoints in monitor-web are hard-coded and both apps must thus run on the same machine :)
