package com.flexsible.graph.graph;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TinkerPopReactorTest {

    @Test
    void testGraphTraversalWithReactor() {
        // TinkerGraph 생성
        Graph graph = TinkerGraph.open();
        GraphTraversalSource g = graph.traversal();

        // Mono로 Vertex 추가
        Mono<Vertex> markoMono = Mono.fromSupplier(() -> graph.addVertex("name", "Marko", "age", 29));
        Mono<Vertex> vadasMono = Mono.fromSupplier(() -> graph.addVertex("name", "Vadas", "age", 27));
        Mono<Vertex> lopMono = Mono.fromSupplier(() -> graph.addVertex("name", "Lop", "lang", "java"));

        // Vertex를 모두 추가한 후 Flux로 처리
        Flux<Vertex> vertexFlux = Flux.concat(markoMono, vadasMono, lopMono);

        // Edge 추가
        Flux<Void> edgeFlux = vertexFlux.collectList().flatMapMany(vertices -> {
            Vertex marko = vertices.get(0);
            Vertex vadas = vertices.get(1);
            Vertex lop = vertices.get(2);

            Mono<Void> knowsEdgeMono = Mono.fromRunnable(() -> marko.addEdge("knows", vadas, "weight", 0.5f));
            Mono<Void> createdEdgeMono = Mono.fromRunnable(() -> marko.addEdge("created", lop, "weight", 0.4f));

            return Flux.concat(knowsEdgeMono, createdEdgeMono);
        });

        // Flux로 Traversal 처리 - Marko가 아는 사람 수 확인
        Mono<Long> knowsCountMono = edgeFlux.then(Mono.defer(() ->
                Mono.fromSupplier(() -> g.V().has("name", "Marko").out("knows").count().next())
        ));

        // 결과 확인
        knowsCountMono.subscribe(knowsCount -> assertEquals(1, knowsCount));

        // Flux로 Traversal 처리 - Marko가 만든 프로젝트의 언어 확인
        Mono<String> projectLangMono = Mono.defer(() ->
                Mono.fromSupplier(() -> g.V().has("name", "Marko").out("created").values("lang").next().toString())
        );

        // 결과 확인
        projectLangMono.subscribe(lang -> assertEquals("java", lang));
    }
}

