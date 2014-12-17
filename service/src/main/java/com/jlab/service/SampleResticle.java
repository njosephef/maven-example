package com.jlab.service;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class SampleResticle extends Verticle {
 
    public void start() {
        HttpServer server = vertx.createHttpServer();
        RouteMatcher routeMatcher = new RouteMatcher();
        routeMatcher.get("/hello", new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                req.response().setStatusCode(200);
                req.response().end("world");
            }
        });
         routeMatcher.delete("/posts", new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                req.response().setStatusCode(401);
                req.response().end("Not allowed user");
            }
        });
 
         routeMatcher.post("/:blogname", new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                req.response().setStatusCode(200);
                String blogName = req.params().get("blogname");
                req.response().end("post " + blogName + " received !");
            }
        });
 
        routeMatcher.get("/:id", new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                req.response().setStatusCode(200);
                Blog blog =  new Blog("rest","scala & vertx");
                JsonObject obj = new JsonObject();
                obj.putString("title", "rest");
                obj.putString("content", "scala & vertx");
                req.response().end(obj.encode());
            }
        });
 
        server.requestHandler(routeMatcher).listen(8080, "localhost");
    }
}