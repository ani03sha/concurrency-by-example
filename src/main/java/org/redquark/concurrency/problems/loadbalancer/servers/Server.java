package org.redquark.concurrency.problems.loadbalancer.servers;

import java.util.concurrent.atomic.AtomicInteger;

public class Server {

    private final String id;
    private final AtomicInteger activeConnections;
    private volatile boolean isUp;

    public Server(String id) {
        this.id = id;
        this.activeConnections = new AtomicInteger(0);
        this.isUp = true;
    }

    public String getId() {
        return this.id;
    }

    public int getActiveConnections() {
        return this.activeConnections.get();
    }

    public boolean isUp() {
        return this.isUp;
    }

    public void setUp(boolean up) {
        this.isUp = up;
    }

    public void addConnection() {
        this.activeConnections.incrementAndGet();
    }

    public void removeConnection() {
        this.activeConnections.decrementAndGet();
    }
}
