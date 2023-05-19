package com.dar.freshmaze.common;

import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Graph<VertexT, EdgeT> {
    final HashMap<VertexT, HashMap<VertexT, EdgeT>> graph = new HashMap<>();

    public Set<Map.Entry<VertexT, HashMap<VertexT, EdgeT>>> entrySet() {
        return graph.entrySet();
    }

    public Map<VertexT, EdgeT> getConnections(VertexT vertex) {
        return Collections.unmodifiableMap(graph.get(vertex));
    }

    public void add(VertexT first, VertexT second, EdgeT edge) {
        addSingle(first, second, edge);
        addSingle(second, first, edge);
    }

    public void remove(VertexT vertex) {
        if (!graph.containsKey(vertex))
            return;

        for (VertexT connectedVertex : graph.get(vertex).keySet())
            graph.get(connectedVertex).remove(vertex);

        graph.remove(vertex);
    }

    private void addSingle(VertexT first, VertexT second, EdgeT edge) {
        final HashMap<VertexT, EdgeT> connections = graph.getOrDefault(first, new HashMap<>());
        connections.put(second, edge);

        graph.put(first, connections);
    }
}
