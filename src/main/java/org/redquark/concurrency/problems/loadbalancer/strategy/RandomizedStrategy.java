package org.redquark.concurrency.problems.loadbalancer.strategy;

import org.redquark.concurrency.problems.loadbalancer.servers.Server;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class RandomizedStrategy implements LoadBalancerStrategy {

    @Override
    public Optional<Server> selectServer(List<Server> servers) {
        // Get the up servers
        final List<Server> upServers = servers.stream().filter(Server::isUp).toList();
        if (upServers.isEmpty()) {
            return Optional.empty();
        }
        final Server selectedServer = upServers.get(ThreadLocalRandom.current().nextInt(upServers.size()));
        return Optional.of(selectedServer);
    }
}
