package org.redquark.concurrency.problems.loadbalancer;

import org.redquark.concurrency.problems.loadbalancer.servers.Server;
import org.redquark.concurrency.problems.loadbalancer.strategy.LoadBalancerStrategy;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LoadBalancer {

    private final ConcurrentHashMap<String, Server> servers = new ConcurrentHashMap<>();
    private LoadBalancerStrategy strategy;

    public LoadBalancer(LoadBalancerStrategy strategy) {
        this.strategy = strategy;
    }

    public void registerServer(Server server) {
        this.servers.put(server.getId(), server);
    }

    public void unregisterServer(Server server) {
        this.servers.remove(server.getId());
    }

    public void setStrategy(LoadBalancerStrategy strategy) {
        this.strategy = strategy;
    }

    public ConcurrentHashMap<String, Server> getServers() {
        return this.servers;
    }

    public void routeRequest() {
        Optional<Server> selectedServer = this.strategy.selectServer(this.getServers().values().stream().toList());
        selectedServer.ifPresent(Server::addConnection);
    }

    public void completeRequest(Server server) {
        server.removeConnection();
    }
}
