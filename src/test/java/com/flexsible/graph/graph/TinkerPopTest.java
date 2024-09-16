package com.flexsible.graph.graph;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TinkerPopTest {

    @Test
    void testGraphTraversal() {
        // TinkerGraph 생성
        Graph graph = TinkerGraph.open();

        // Vertex 추가
        Vertex marko = graph.addVertex("name", "Marko", "age", 29);
        Vertex vadas = graph.addVertex("name", "Vadas", "age", 27);
        Vertex lop = graph.addVertex("name", "Lop", "lang", "java");

        // Edge 추가
        marko.addEdge("knows", vadas, "weight", 0.5f);
        marko.addEdge("created", lop, "weight", 0.4f);

        // Traversal source 생성
        GraphTraversalSource g = graph.traversal();

        // Traversal을 사용하여 Marko가 아는 사람 찾기
        long knowsCount = g.V(marko).out("knows").count().next();
        assertEquals(1, knowsCount);

        // Marko가 만든 프로젝트 찾기
        String projectLang = g.V(marko).out("created").values("lang").next().toString();
        assertEquals("java", projectLang);
    }
}
