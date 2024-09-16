package com.flexsible.graph.graph;

import com.flexsible.graph.user.domain.User;
import com.flexsible.graph.user.domain.UserGroup;
import com.flexsible.graph.user.domain.UserGroupClosure;
import com.flexsible.graph.user.persistent.UserGroupClosureRepository;
import com.flexsible.graph.user.persistent.UserGroupRepository;
import com.flexsible.graph.user.persistent.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class UserGroupSaveTestsAll {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserGroupClosureRepository userGroupClosureRepository;

    @BeforeEach
    void setUp() {
        // 데이터 클리어
        userRepository.deleteAll().block();
        userGroupRepository.deleteAll().block();
        userGroupClosureRepository.deleteAll().block();
    }

    @Test
    void testUserSave() {
        // 1. 유저 저장 (빌더 패턴 사용)
        Mono<User> userMono = userRepository.save(
                User.builder()
                .username("Jane Doe")
                .build());

        // 검증
        StepVerifier.create(userMono)
                .expectNextMatches(user -> user.getUsername().equals("Jane Doe"))
                .verifyComplete();
    }

    @Test
    void testUserGroupSave() {
        // 2. 유저 그룹 저장 (빌더 패턴 사용)
        Mono<UserGroup> userGroupMono = userGroupRepository.save(
                UserGroup.builder()
                        .groupName("Test Group")
                        .build()
        );

        // 검증
        StepVerifier.create(userGroupMono)
                .expectNextMatches(group -> group.getGroupName().equals("Test Group"))
                .verifyComplete();
    }

    @Test
    void testUserGroupClosureSave() {
        // 3. 유저 그룹 클로저 저장 (빌더 패턴 사용)
        Mono<UserGroup> parentGroupMono = userGroupRepository.save(
                UserGroup.builder()
                        .groupName("Parent Group")
                        .build()
        );
        Mono<UserGroup> childGroupMono = userGroupRepository.save(
                UserGroup.builder()
                        .groupName("Child Group")
                        .build()
        );

        // 두 그룹 저장 후 클로저 생성
        Mono<UserGroupClosure> userGroupClosureMono = parentGroupMono.zipWith(childGroupMono)
                .flatMap(tuple -> {
                    UserGroup parentGroup = tuple.getT1();
                    UserGroup childGroup = tuple.getT2();
                    return userGroupClosureRepository.save(
                            UserGroupClosure.builder()
                                    .ancestorGroupId(parentGroup.getGroupId())
                                    .descendantGroupId(childGroup.getGroupId())
                                    .distance(1)
                                    .build()
                    );
                });

        // 검증
        StepVerifier.create(userGroupClosureMono)
                .expectNextMatches(closure -> closure.getDistance() == 1)
                .verifyComplete();
    }
}
