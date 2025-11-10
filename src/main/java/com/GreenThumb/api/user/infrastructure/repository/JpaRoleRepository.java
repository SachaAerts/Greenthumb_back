package com.GreenThumb.api.user.infrastructure.repository;

import com.GreenThumb.api.user.domain.exception.NoFoundException;
import com.GreenThumb.api.user.domain.repository.RoleRepository;
import com.GreenThumb.api.user.infrastructure.entity.RoleEntity;
import org.springframework.stereotype.Repository;

@Repository
public class JpaRoleRepository implements RoleRepository {
    private final SpringDataRoleRepository jpaRepo;

    public JpaRoleRepository(SpringDataRoleRepository jpaRoleRepository) {
        this.jpaRepo = jpaRoleRepository;
    }

    @Override
    public RoleEntity getRoleEntity(String label){
        return jpaRepo.findByLabel(label)
                .orElseThrow(() -> new NoFoundException("Le role est introuvable"));
    }
}
