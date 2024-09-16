package com.flexsible.graph.graph;

import com.flexsible.graph.user.domain.UserGroup;
import com.flexsible.graph.user.domain.UserGroupClosure;
import com.flexsible.graph.user.persistent.UserGroupClosureRepository;
import com.flexsible.graph.user.persistent.UserGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class UserGroupSaveTestsAll2 {

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserGroupClosureRepository userGroupClosureRepository;

    @BeforeEach
    void setUp() {
        // 데이터 클리어
        userGroupRepository.deleteAll().block();
        userGroupClosureRepository.deleteAll().block();
    }

    // 그룹이 존재하지 않으면 새로 삽입
    private Mono<UserGroup> saveGroupIfNotExist(String groupName) {
        return userGroupRepository.findByGroupName(groupName)
                .switchIfEmpty(userGroupRepository.save(
                        UserGroup.builder()
                                .groupName(groupName)
                                .build()
                ));
    }

    @Test
    void testDeepGroupHierarchySaveWithMultipleDepthsAndDistances() {
        // 루트 그룹 생성
        Mono<UserGroup> rootGroupMono = saveGroupIfNotExist("Root Group");

        // 1부터 10단계 그룹을 생성
        Mono<Void> groupHierarchySave = rootGroupMono.flatMap(rootGroup -> {

            // 그룹 생성 및 관계 저장을 위한 초기값 설정
            Mono<UserGroup> previousGroupMono = Mono.just(rootGroup);

            for (int depth = 1; depth <= 10; depth++) {
                final int currentDepth = depth;  // depth를 final로 선언하여 람다에서 사용 가능하게 함
                String groupName = "Group Level " + currentDepth;

                // 각 레벨의 그룹 생성 및 클로저 저장
                previousGroupMono = previousGroupMono.flatMap(ancestorGroup ->
                        saveGroupIfNotExist(groupName).flatMap(descendantGroup ->
                                Mono.fromCallable(() -> {
                                    // 1단계~currentDepth까지의 관계를 저장
                                    for (int distance = 1; distance <= currentDepth; distance++) {
                                        // 상위 그룹과 하위 그룹의 클로저 저장
                                        userGroupClosureRepository.save(
                                                UserGroupClosure.builder()
                                                        .ancestorGroupId(ancestorGroup.getGroupId())
                                                        .descendantGroupId(descendantGroup.getGroupId())
                                                        .distance(distance)
                                                        .build()
                                        ).subscribe();  // 비동기로 실행
                                    }
                                    return descendantGroup;
                                }).then(Mono.just(descendantGroup)) // 다음 그룹을 이전 그룹으로 설정
                        )
                );
            }

            return previousGroupMono.then();
        });

        // 검증
        StepVerifier.create(groupHierarchySave)
                .verifyComplete();
    }

}
