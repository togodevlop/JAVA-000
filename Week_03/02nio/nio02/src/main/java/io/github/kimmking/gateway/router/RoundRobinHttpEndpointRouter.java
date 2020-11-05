package io.github.kimmking.gateway.router;

import java.util.List;


public class RoundRobinHttpEndpointRouter implements HttpEndpointRouter {
    private int state = 0;

    @Override
    public String route(List<String> endpoints) {
        int size = endpoints.size();
        return endpoints.get(Math.abs((state + 1) % size));
    }
}
