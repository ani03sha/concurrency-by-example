package org.redquark.concurrency.problems.loadbalancer.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redquark.concurrency.problems.loadbalancer.servers.Server;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LeastConnectionStrategyTest {

    private LeastConnectionStrategy leastConnectionStrategy;

    private Server server1;
    private Server server2;
    private Server server3;


    @BeforeEach
    void setUp() {
        leastConnectionStrategy = new LeastConnectionStrategy();

        server1 = mock(Server.class);
        server2 = mock(Server.class);
        server3 = mock(Server.class);
    }

    @Test
    void shouldReturnServersWithLeastConnections() {
        when(server1.isUp()).thenReturn(true);
        when(server2.isUp()).thenReturn(true);
        when(server3.isUp()).thenReturn(true);

        when(server1.getActiveConnections()).thenReturn(5);
        when(server2.getActiveConnections()).thenReturn(2);
        when(server3.getActiveConnections()).thenReturn(7);

        List<Server> servers = List.of(server1, server2, server3);
        assertEquals(Optional.of(server2), leastConnectionStrategy.selectServer(servers));
    }

    @Test
    void shouldIgnoreDownServers() {
        when(server1.isUp()).thenReturn(false);
        when(server2.isUp()).thenReturn(true);

        when(server2.getActiveConnections()).thenReturn(3);

        List<Server> servers = List.of(server1, server2);
        assertEquals(Optional.of(server2), leastConnectionStrategy.selectServer(servers));
    }

    @Test
    void shouldReturnNullIfNoServerIsUp() {
        when(server1.isUp()).thenReturn(false);
        when(server2.isUp()).thenReturn(false);

        when(server1.getActiveConnections()).thenReturn(5);
        when(server2.getActiveConnections()).thenReturn(2);

        List<Server> servers = List.of(server1, server2);
        assertEquals(Optional.empty(), leastConnectionStrategy.selectServer(servers));
    }
}