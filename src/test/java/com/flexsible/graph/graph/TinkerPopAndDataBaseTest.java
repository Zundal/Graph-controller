package com.flexsible.graph.graph;

import com.flexsible.graph.user.domain.UserGroup;
import com.flexsible.graph.user.domain.UserGroupClosure;
import com.flexsible.graph.user.persistent.UserGroupClosureRepository;
import com.flexsible.graph.user.persistent.UserGroupRepository;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class TinkerPopAndDataBaseTest {

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserGroupClosureRepository userGroupClosureRepository;

    @Test
    void testGraphTraversalWithDatabaseData() throws InterruptedException {
        // 비동기 작업이 완료될 때까지 기다리기 위한 CountDownLatch 생성
        CountDownLatch latch = new CountDownLatch(1);

        // 1. TinkerGraph 생성
        Graph graph = TinkerGraph.open();

        // 그룹의 Vertex를 저장할 맵
        Map<Integer, Vertex> groupVertices = new HashMap<>();

        // 2. UserGroup 데이터를 데이터베이스에서 조회하고 TinkerGraph에 추가
        Flux<UserGroup> userGroups = userGroupRepository.findAll()
                .doOnNext(userGroup -> {
                    // 각 그룹을 Vertex로 추가
                    Vertex vertex = graph.addVertex("group_name", userGroup.getGroupName(), "group_id", userGroup.getGroupId());
                    groupVertices.put(userGroup.getGroupId(), vertex);
                    // 그룹 추가 로그 출력
                    System.out.println("그룹 추가됨: " + userGroup.getGroupName() + " (ID: " + userGroup.getGroupId() + ")");
                });

        // 3. UserGroupClosure 데이터를 조회하고 상위-하위 관계를 Edge로 추가
        Flux<UserGroupClosure> userGroupClosures = userGroupClosureRepository.findAll()
                .doOnNext(closure -> {
                    // 상위 그룹과 하위 그룹 간의 관계를 TinkerGraph에 Edge로 추가
                    Vertex ancestorVertex = groupVertices.get(closure.getAncestorGroupId());
                    Vertex descendantVertex = groupVertices.get(closure.getDescendantGroupId());
                    if (ancestorVertex != null && descendantVertex != null) {
                        ancestorVertex.addEdge("has_child", descendantVertex, "distance", closure.getDistance());
                        // 관계 추가 로그 출력
                        System.out.println("Edge 추가됨: " +
                                "상위 그룹: " + ancestorVertex.property("group_name").value() + " -> " +
                                "하위 그룹: " + descendantVertex.property("group_name").value() +
                                " (distance: " + closure.getDistance() + ")");
                    } else {
                        System.out.println("Edge 추가 실패: 상위 그룹 또는 하위 그룹이 존재하지 않음");
                    }
                });

        // 4. 모든 Flux가 완료된 후 TinkerGraph에서 데이터 탐색
        Mono<Void> setupGraph = userGroups.thenMany(userGroupClosures).then();

        // 5. Gremlin 트래버설을 사용하여 탐색
        setupGraph.subscribe(v -> {
            GraphTraversalSource g = graph.traversal();

            // 예: Root Group의 모든 하위 그룹을 탐색
            String rootGroupName = "Root Group";
            System.out.println("Root Group의 하위 그룹:");

            // Root Group의 하위 그룹을 탐색
            g.V().has("group_name", rootGroupName)
                    .outE("has_child") // Root Group에서 나가는 엣지
                    .forEachRemaining(edge -> {
                        Vertex childVertex = edge.inVertex();  // 하위 그룹 Vertex
                        Object distance = edge.property("distance").value();  // 거리 값

                        System.out.println("하위 그룹: " + childVertex.property("group_name").value() +
                                ", distance: " + distance);
                    });

            // 모든 작업이 완료되면 CountDownLatch 감소
            latch.countDown();
        });

        // 비동기 작업이 완료될 때까지 대기 (최대 5초)
        latch.await(5, TimeUnit.SECONDS);
    }
}
