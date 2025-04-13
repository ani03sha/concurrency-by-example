package org.redquark.concurrency.problems.loadbalancer.strategy;

import org.redquark.concurrency.problems.loadbalancer.servers.Server;

import java.util.List;
import java.util.Optional;

public interface LoadBalancerStrategy {

    Optional<Server> selectServer(List<Server> servers);
}
