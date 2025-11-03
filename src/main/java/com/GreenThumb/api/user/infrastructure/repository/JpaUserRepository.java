package com.GreenThumb.api.user.infrastructure.repository;

import com.GreenThumb.api.user.domain.entity.User;
import com.GreenThumb.api.user.domain.exception.FormatException;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import com.GreenThumb.api.user.domain.repository.UserRepository;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import com.GreenThumb.api.user.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JpaUserRepository implements UserRepository {
    private final SpringDataUserRepository jpaRepo;

    public JpaUserRepository(SpringDataUserRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public String getUsername(Long id_user) {
        return jpaRepo.findById(id_user)
                .map(UserEntity::getUsername)
                .orElseThrow(() -> new NoFoundException("User not found with id " + id_user));
    }

    @Override
    public User getUserByEmail(String email) throws NoFoundException, IllegalArgumentException {
        return jpaRepo.findByMail(email)
                .map(userEntity -> {
                    try {
                        return UserMapper.toDomain(userEntity);
                    } catch (FormatException e) {
                        throw new IllegalArgumentException("Erreur de format interne", e);
                    }
                })
                .orElseThrow(() -> new NoFoundException("L'utilisateur n'a pas été trouvé"));
    }

    @Override
    public long count() {
        return jpaRepo.count();
    }

}
