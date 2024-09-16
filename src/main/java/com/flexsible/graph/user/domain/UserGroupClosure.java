package com.flexsible.graph.user.domain;

import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table("user_group_closure")
public class UserGroupClosure {

    private Integer ancestorGroupId;  // 상위 그룹 ID

    private Integer descendantGroupId;  // 하위 그룹 ID

    private Integer distance;  // 상위 그룹과 하위 그룹 간의 거리
}
