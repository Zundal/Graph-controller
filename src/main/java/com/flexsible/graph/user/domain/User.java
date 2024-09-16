package com.flexsible.graph.user.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Builder
@ToString
@Table("user")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private Integer userId;  // 유저 ID (Primary Key)

    private String username;  // 그룹 이름
}
