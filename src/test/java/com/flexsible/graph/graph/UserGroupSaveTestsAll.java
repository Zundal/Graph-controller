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
public class UserGroupSaveTestsAll {

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
    void testDeepGroupHierarchySaveWithMultipleDepths() {
        // 1. 루트 그룹 생성
        Mono<UserGroup> rootGroupMono = saveGroupIfNotExist("Root Group");

        // 2. 첫 번째 레벨의 중간 그룹 생성 (뎁스 1)
        Mono<UserGroup> middleGroup1Mono = saveGroupIfNotExist("Middle Group 1");
        Mono<UserGroup> middleGroup2Mono = saveGroupIfNotExist("Middle Group 2");

        // 3. 두 번째 레벨의 하위 그룹 생성 (뎁스 2)
        Mono<UserGroup> leafGroup1Mono = saveGroupIfNotExist("Leaf Group 1");
        Mono<UserGroup> leafGroup2Mono = saveGroupIfNotExist("Leaf Group 2");
        Mono<UserGroup> leafGroup3Mono = saveGroupIfNotExist("Leaf Group 3");

        // 각 그룹 간의 관계 설정 및 클로저 저장
        Mono<Void> groupHierarchySave = rootGroupMono.zipWith(middleGroup1Mono)
                .flatMap(tuple -> {
                    UserGroup rootGroup = tuple.getT1();
                    UserGroup middleGroup1 = tuple.getT2();

                    // 루트 그룹과 첫 번째 중간 그룹의 클로저 저장 (distance = 1)
                    return userGroupClosureRepository.save(
                            UserGroupClosure.builder()
                                    .ancestorGroupId(rootGroup.getGroupId())
                                    .descendantGroupId(middleGroup1.getGroupId())
                                    .distance(1)
                                    .build()
                    );
                })
                .then(rootGroupMono.zipWith(middleGroup2Mono)
                        .flatMap(tuple -> {
                            UserGroup rootGroup = tuple.getT1();
                            UserGroup middleGroup2 = tuple.getT2();

                            // 루트 그룹과 두 번째 중간 그룹의 클로저 저장 (distance = 1)
                            return userGroupClosureRepository.save(
                                    UserGroupClosure.builder()
                                            .ancestorGroupId(rootGroup.getGroupId())
                                            .descendantGroupId(middleGroup2.getGroupId())
                                            .distance(1)
                                            .build()
                            );
                        })
                )
                .then(middleGroup1Mono.zipWith(leafGroup1Mono)
                        .flatMap(tuple -> {
                            UserGroup middleGroup1 = tuple.getT1();
                            UserGroup leafGroup1 = tuple.getT2();

                            // 첫 번째 중간 그룹과 첫 번째 하위 그룹의 클로저 저장 (distance = 1)
                            return userGroupClosureRepository.save(
                                    UserGroupClosure.builder()
                                            .ancestorGroupId(middleGroup1.getGroupId())
                                            .descendantGroupId(leafGroup1.getGroupId())
                                            .distance(1)
                                            .build()
                            );
                        })
                )
                .then(middleGroup1Mono.zipWith(leafGroup2Mono)
                        .flatMap(tuple -> {
                            UserGroup middleGroup1 = tuple.getT1();
                            UserGroup leafGroup2 = tuple.getT2();

                            // 첫 번째 중간 그룹과 두 번째 하위 그룹의 클로저 저장 (distance = 1)
                            return userGroupClosureRepository.save(
                                    UserGroupClosure.builder()
                                            .ancestorGroupId(middleGroup1.getGroupId())
                                            .descendantGroupId(leafGroup2.getGroupId())
                                            .distance(1)
                                            .build()
                            );
                        })
                )
                .then(middleGroup2Mono.zipWith(leafGroup3Mono)
                        .flatMap(tuple -> {
                            UserGroup middleGroup2 = tuple.getT1();
                            UserGroup leafGroup3 = tuple.getT2();

                            // 두 번째 중간 그룹과 세 번째 하위 그룹의 클로저 저장 (distance = 1)
                            return userGroupClosureRepository.save(
                                    UserGroupClosure.builder()
                                            .ancestorGroupId(middleGroup2.getGroupId())
                                            .descendantGroupId(leafGroup3.getGroupId())
                                            .distance(1)
                                            .build()
                            );
                        })
                )
                .then(rootGroupMono.zipWith(leafGroup1Mono)
                        .flatMap(tuple -> {
                            UserGroup rootGroup = tuple.getT1();
                            UserGroup leafGroup1 = tuple.getT2();

                            // 루트 그룹과 첫 번째 하위 그룹의 클로저 저장 (distance = 2)
                            return userGroupClosureRepository.save(
                                    UserGroupClosure.builder()
                                            .ancestorGroupId(rootGroup.getGroupId())
                                            .descendantGroupId(leafGroup1.getGroupId())
                                            .distance(2)
                                            .build());
                        })
                )
                .then(rootGroupMono.zipWith(leafGroup2Mono)
                        .flatMap(tuple -> {
                            UserGroup rootGroup = tuple.getT1();
                            UserGroup leafGroup2 = tuple.getT2();

                            // 루트 그룹과 두 번째 하위 그룹의 클로저 저장 (distance = 2)
                            return userGroupClosureRepository.save(
                                    UserGroupClosure.builder()
                                            .ancestorGroupId(rootGroup.getGroupId())
                                            .descendantGroupId(leafGroup2.getGroupId())
                                            .distance(2)
                                            .build());
                        })
                )
                .then(rootGroupMono.zipWith(leafGroup3Mono)
                        .flatMap(tuple -> {
                            UserGroup rootGroup = tuple.getT1();
                            UserGroup leafGroup3 = tuple.getT2();

                            // 루트 그룹과 세 번째 하위 그룹의 클로저 저장 (distance = 2)
                            return userGroupClosureRepository.save(
                                    UserGroupClosure.builder()
                                            .ancestorGroupId(rootGroup.getGroupId())
                                            .descendantGroupId(leafGroup3.getGroupId())
                                            .distance(2)
                                            .build());
                        })
                ).then();  // 모든 작업 완료 후 종료

        // 검증
        StepVerifier.create(groupHierarchySave)
                .verifyComplete();
    }

}
