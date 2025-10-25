package com.Greanthumb.api.user.infrastructure.repository;

import com.Greanthumb.api.user.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaUserRepository implements UserRepository {
    private final SpringDataUserRepository jpaRepo;

    public JpaUserRepository(SpringDataUserRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public long count() {
        return jpaRepo.count();
    }

}
