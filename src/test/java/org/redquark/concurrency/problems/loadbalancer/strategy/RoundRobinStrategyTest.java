package org.redquark.concurrency.problems.loadbalancer.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redquark.concurrency.problems.loadbalancer.servers.Server;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RoundRobinStrategyTest {

    private RoundRobinStrategy roundRobinStrategy;

    private Server server1;
    private Server server2;
    private Server server3;

    @BeforeEach
    void setUp() {
        roundRobinStrategy = new RoundRobinStrategy();

        server1 = mock(Server.class);
        server2 = mock(Server.class);
        server3 = mock(Server.class);
    }

    @Test
    void shouldReturnServersInRoundRobinOrder() {
        when(server1.isUp()).thenReturn(true);
        when(server2.isUp()).thenReturn(true);
        when(server3.isUp()).thenReturn(true);

        List<Server> servers = List.of(server1, server2, server3);

        assertEquals(Optional.of(server1), roundRobinStrategy.selectServer(servers));
        assertEquals(Optional.of(server2), roundRobinStrategy.selectServer(servers));
        assertEquals(Optional.of(server3), roundRobinStrategy.selectServer(servers));
        assertEquals(Optional.of(server1), roundRobinStrategy.selectServer(servers));
    }

    @Test
    void shouldSkipDownServers() {
        when(server1.isUp()).thenReturn(false);
        when(server2.isUp()).thenReturn(true);

        List<Server> servers = List.of(server1, server2);
        assertEquals(Optional.of(server2), roundRobinStrategy.selectServer(servers));
    }

    @Test
    void shouldReturnNullIfNoServerIsUp() {
        when(server1.isUp()).thenReturn(false);
        when(server2.isUp()).thenReturn(false);

        List<Server> servers = List.of(server1, server2);
        assertEquals(Optional.empty(), roundRobinStrategy.selectServer(servers));
    }
}