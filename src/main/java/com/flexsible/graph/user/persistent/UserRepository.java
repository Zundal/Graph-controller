package com.flexsible.graph.user.persistent;

import com.flexsible.graph.user.domain.User;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
    extends R2dbcRepository<User, Integer> {

}
