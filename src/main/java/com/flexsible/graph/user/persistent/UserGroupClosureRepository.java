package com.flexsible.graph.user.persistent;

import com.flexsible.graph.user.domain.UserGroupClosure;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupClosureRepository
    extends R2dbcRepository<UserGroupClosure, Integer> {

}
