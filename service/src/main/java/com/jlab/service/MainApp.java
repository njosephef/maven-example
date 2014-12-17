package com.jlab.service;

/**
 * Created by scorpiovn on 12/17/14.
 */
class MainApp {
    public static void main(String[] args) {
        SampleResticle sampleResticle = new SampleResticle();
//        sampleResticle.start();

        PingVerticle pingVerticle = new PingVerticle();
        pingVerticle.start();
    }
}
