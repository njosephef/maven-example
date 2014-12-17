package com.jlab.service;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

public class SampleHttpWebServer extends Verticle {
 
    public void start() {
        HttpServer server = vertx.createHttpServer();
        final Logger log = getContainer().logger();
        server.requestHandler(new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest request) {
                log.info("A request has arrived on the server!");
            }
        }).listen(8080, "localhost");
    }
}