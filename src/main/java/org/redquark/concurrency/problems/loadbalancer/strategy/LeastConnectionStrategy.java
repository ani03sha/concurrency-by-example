package org.redquark.concurrency.problems.loadbalancer.strategy;

import org.redquark.concurrency.problems.loadbalancer.servers.Server;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class LeastConnectionStrategy implements LoadBalancerStrategy {
    @Override
    public Optional<Server> selectServer(List<Server> servers) {
        Server selectedServer = servers
                .stream()
                .filter(Server::isUp)
                .min(Comparator.comparingInt(Server::getActiveConnections))
                .orElse(null);
        if (selectedServer == null) {
            return Optional.empty();
        }
        return Optional.of(selectedServer);
    }
}
