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
public class UserGroupSaveTests {

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
                        .build()
        );

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
                .build());

        // 검증
        StepVerifier.create(userGroupMono)
                .expectNextMatches(group -> group.getGroupName().equals("Test Group"))
                .verifyComplete();
    }

    @Test
    void testUserGroupClosureSave() {
        // 1. 저장할 유저 그룹 생성
        Mono<UserGroup> parentGroupMono = userGroupRepository.save(
                UserGroup.builder()
                        .groupName("Parent Group")
                        .build());

        Mono<UserGroup> childGroupMono = userGroupRepository.save(
                UserGroup.builder()
                        .groupName("Child Group")
                        .build());

        // 2. 저장 후 그룹 다시 조회 (정확한 ID 가져오기)
        Mono<UserGroupClosure> userGroupClosureMono = parentGroupMono
                .flatMap(savedParentGroup ->
                        childGroupMono.flatMap(savedChildGroup -> {
                            return userGroupClosureRepository.save(
                                    UserGroupClosure.builder()
                                            .ancestorGroupId(savedParentGroup.getGroupId())  // 저장 후 가져온 부모 그룹 ID 사용
                                            .descendantGroupId(savedChildGroup.getGroupId())  // 저장 후 가져온 자식 그룹 ID 사용
                                            .distance(1)
                                            .build()
                            );
                        })
                );

        // 3. 검증
        StepVerifier.create(userGroupClosureMono)
                .expectNextMatches(closure -> closure.getDistance() == 1)
                .verifyComplete();
    }

    @Test
    void closeInsert() {

        UserGroupClosure insertData = UserGroupClosure.builder()
                .ancestorGroupId(1)
                .descendantGroupId(2)
                .distance(1)
                .build();

        userGroupClosureRepository.save(insertData)
                .block();
    }

}
