package com.flexsible.graph.user.persistent;

import com.flexsible.graph.user.domain.UserGroup;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupRepository
    extends R2dbcRepository<UserGroup, Integer> {

}
