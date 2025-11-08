package com.GreenThumb.api.user.infrastructure.repository;

import com.GreenThumb.api.user.domain.entity.User;
import com.GreenThumb.api.user.domain.exception.FormatException;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import com.GreenThumb.api.user.domain.repository.UserRepository;
import com.GreenThumb.api.user.domain.service.PasswordService;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import com.GreenThumb.api.user.infrastructure.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
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
                .orElseThrow(() -> new NoFoundException("L'utilisateur n'a pas été trouvé"));
    }

    @Override
    public User getUserByEmail(String email, String password) throws NoFoundException, IllegalArgumentException {
        return jpaRepo.findByMail(email)
                .map(userEntity -> {
                    checkPassword(password, userEntity);

                    try {
                        return UserMapper.toDomain(userEntity);
                    } catch (FormatException e) {
                        throw new IllegalArgumentException("Erreur de format interne", e);
                    }
                })
                .orElseThrow(() -> new NoFoundException("L'utilisateur n'a pas été trouvé"));
    }

    @Override
    public User getUserByUsernameAndPassword(String username, String password) throws NoFoundException, IllegalArgumentException {
        return jpaRepo.findByUsername(username)
                .map(userEntity -> {
                    checkPassword(password, userEntity);

                    try {
                        return UserMapper.toDomain(userEntity);
                    } catch (FormatException e) {

                        throw new IllegalArgumentException("Erreur de format interne", e);
                    }
                })
                .orElseThrow(() -> new NoFoundException("L'utilisateur n'a pas été trouvé"));
    }

    public User getUserByUsername(String username) throws NoFoundException, IllegalArgumentException {
        return jpaRepo.findByUsername(username)
                .map(userEntity -> {
                    try {
                        return UserMapper.toDomain(userEntity);
                    } catch (FormatException e) {
                        throw new IllegalArgumentException("Erreur de format interne", e);
                    }
                })
                .orElseThrow(() ->  new NoFoundException("L'utilisateur n'a pas été trouvé"));
    }

    @Override
    public long count() {
        return jpaRepo.count();
    }


    private void checkPassword(String password, UserEntity userEntity) {
        if (!PasswordService.verify(userEntity.getPassword(), password)) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }
    }

}
