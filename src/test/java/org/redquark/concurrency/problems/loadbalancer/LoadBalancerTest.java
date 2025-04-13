package org.redquark.concurrency.problems.loadbalancer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redquark.concurrency.problems.loadbalancer.servers.Server;
import org.redquark.concurrency.problems.loadbalancer.strategy.LoadBalancerStrategy;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoadBalancerTest {

    private LoadBalancer loadBalancer;
    private LoadBalancerStrategy mockStrategy;

    private Server server1;
    private Server server2;

    @BeforeEach
    void setUp() {
        mockStrategy = mock(LoadBalancerStrategy.class);
        loadBalancer = new LoadBalancer(mockStrategy);

        server1 = mock(Server.class);
        server2 = mock(Server.class);

        when(server1.getId()).thenReturn("s1");
        when(server1.getActiveConnections()).thenReturn(0);
        when(server1.isUp()).thenReturn(true);
        when(server2.getId()).thenReturn("s2");
        when(server2.getActiveConnections()).thenReturn(0);
        when(server2.isUp()).thenReturn(true);
    }

    @Test
    void shouldCallStrategyWithServerListWhenRouting() {
        loadBalancer.registerServer(server1);
        loadBalancer.registerServer(server2);

        when(mockStrategy.selectServer(anyList())).thenReturn(Optional.of(server1));

        loadBalancer.routeRequest();

        verify(mockStrategy, times(1)).selectServer(anyList());
    }

    @Test
    void shouldAddConnectionToSelectedServer() {
        loadBalancer.registerServer(server1);
        loadBalancer.registerServer(server2);

        when(mockStrategy.selectServer(anyList())).thenReturn(Optional.of(server1));

        loadBalancer.routeRequest();

        verify(server1, times(1)).addConnection();
        verify(server2, never()).addConnection();
    }

    @Test
    void shouldNotAddConnectionIfNoServerIsSelected() {
        loadBalancer.registerServer(server1);

        when(mockStrategy.selectServer(anyList())).thenReturn(Optional.empty());

        loadBalancer.routeRequest();

        verify(server1, never()).addConnection();
    }

    @Test
    void shouldRemoveConnectionOnCompleteRequest() {
        loadBalancer.registerServer(server1);
        loadBalancer.completeRequest(server1);
        verify(server1, times(1)).removeConnection();
    }
}