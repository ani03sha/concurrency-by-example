package org.redquark.concurrency.problems.loadbalancer.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redquark.concurrency.problems.loadbalancer.servers.Server;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class RandomizedStrategyTest {

    private RandomizedStrategy randomizedStrategy;

    private Server server1;
    private Server server2;
    private Server server3;

    @BeforeEach
    void setUp() {
        randomizedStrategy = new RandomizedStrategy();

        server1 = mock(Server.class);
        server2 = mock(Server.class);
        server3 = mock(Server.class);
    }

    @Test
    void shouldReturnOnlyUpServers() {
        when(server1.isUp()).thenReturn(true);
        when(server1.isUp()).thenReturn(false);
        when(server1.isUp()).thenReturn(true);

        List<Server> servers = List.of(server1, server2, server3);

        // Run multiple times to expect randomness
        for (int i = 0; i < 20; i++) {
            Optional<Server> selected = randomizedStrategy.selectServer(servers);
            assertTrue(Optional.of(server1).equals(selected) || Optional.of(server3).equals(selected));
        }
    }

    @Test
    void shouldReturnNullIfNoServerIsUp() {
        when(server1.isUp()).thenReturn(false);
        when(server2.isUp()).thenReturn(false);
        when(server3.isUp()).thenReturn(false);

        List<Server> servers = List.of(server1, server2, server3);

        assertEquals(Optional.empty(), randomizedStrategy.selectServer(servers));
    }
}