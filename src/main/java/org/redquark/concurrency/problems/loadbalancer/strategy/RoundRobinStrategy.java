package org.redquark.concurrency.problems.loadbalancer.strategy;

import org.redquark.concurrency.problems.loadbalancer.servers.Server;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinStrategy implements LoadBalancerStrategy {

    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public Optional<Server> selectServer(List<Server> servers) {
        // Get the list of servers that are up
        final List<Server> upServers = servers.stream().filter(Server::isUp).toList();
        if (upServers.isEmpty()) {
            return Optional.empty();
        }
        final int currentIndex = Math.abs(index.getAndIncrement());
        final Server selectedServer = upServers.get(currentIndex % upServers.size());
        return Optional.of(selectedServer);
    }
}
