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
        final Map<VertexT, EdgeT> connections = graph.get(vertex);
        if (connections == null)
            return null;

        return Collections.unmodifiableMap(connections);
    }

    public boolean contains(VertexT first) {
        return graph.containsKey(first);
    }

    public void add(VertexT first, VertexT second, EdgeT edge) {
        addDirected(first, second, edge);
        addDirected(second, first, edge);
    }

    public void addDirected(VertexT first, VertexT second, EdgeT edge) {
        final HashMap<VertexT, EdgeT> connections = graph.getOrDefault(first, new HashMap<>());
        connections.put(second, edge);

        graph.put(first, connections);
    }

    public void remove(VertexT vertex) {
        if (!graph.containsKey(vertex))
            return;

        for (VertexT connectedVertex : graph.get(vertex).keySet())
            graph.get(connectedVertex).remove(vertex);

        graph.remove(vertex);
    }
}
