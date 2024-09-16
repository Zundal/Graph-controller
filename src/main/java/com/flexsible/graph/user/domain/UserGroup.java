package com.flexsible.graph.user.domain;

import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table("user_group")
public class UserGroup {

    @Id
    private Integer groupId;  // 그룹 ID (Primary Key)

    private String groupName;  // 그룹 이름
}
