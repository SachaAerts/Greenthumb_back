package com.GreenThumb.api.user.infrastructure.repository;

import com.GreenThumb.api.user.application.dto.UserRegister;
import com.GreenThumb.api.user.domain.entity.User;
import com.GreenThumb.api.user.domain.exception.EmailAlreadyUsedException;
import com.GreenThumb.api.user.domain.exception.FormatException;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import com.GreenThumb.api.user.domain.exception.PhoneNumberAlreadyUsedException;
import com.GreenThumb.api.user.domain.repository.RoleRepository;
import com.GreenThumb.api.user.domain.repository.UserRepository;
import com.GreenThumb.api.user.domain.service.AvatarStorageService;
import com.GreenThumb.api.user.domain.service.PasswordService;
import com.GreenThumb.api.user.infrastructure.entity.RoleEntity;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import com.GreenThumb.api.user.infrastructure.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.GreenThumb.api.user.domain.service.PasswordService.hash;

@Slf4j
@Repository
public class JpaUserRepository implements UserRepository {
    private final SpringDataUserRepository jpaRepo;
    private final RoleRepository roleRepository;
    private final AvatarStorageService avatarStorageService;

    public JpaUserRepository(SpringDataUserRepository jpaRepo, RoleRepository roleRepository, AvatarStorageService avatarStorageService) {
        this.jpaRepo = jpaRepo;
        this.roleRepository = roleRepository;
        this.avatarStorageService = avatarStorageService;
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
                        log.warn("Erreur de format pour user username='{}', email='{}', id={}: {} - phoneNumber='{}', enabled={}",
                                username,
                                userEntity.getMail(),
                                userEntity.getId(),
                                e.getMessage(),
                                userEntity.getPhoneNumber(),
                                userEntity.isEnabled());
                        throw new IllegalArgumentException("Erreur de format interne", e);
                    }
                })
                .orElseThrow(() -> new NoFoundException("L'utilisateur n'a pas été trouvé"));
    }

    @Override
    public User findByEmail(String email) throws NoFoundException {
        return jpaRepo.findByMail(email)
                .map(userEntity -> {
                    try {
                        return UserMapper.toDomain(userEntity);
                    } catch (FormatException e) {
                        log.warn("Erreur de format pour user email='{}', username='{}', id={}: {} - phoneNumber='{}', enabled={}",
                                email,
                                userEntity.getUsername(),
                                userEntity.getId(),
                                e.getMessage(),
                                userEntity.getPhoneNumber(),
                                userEntity.isEnabled());
                        throw new IllegalArgumentException("Erreur de format interne", e);
                    }
                })
                .orElseThrow(() -> new NoFoundException("L'utilisateur n'a pas été trouvé"));
    }

    @Override
    public void enableUser(String email) throws NoFoundException {
        UserEntity user = jpaRepo.findByMail(email)
                .orElseThrow(() -> new NoFoundException("L'utilisateur n'a pas été trouvé"));
        user.setEnabled(true);
        jpaRepo.save(user);
    }

    @Override
    public boolean isUserEnabled(String email) throws NoFoundException {
        return jpaRepo.findByMail(email)
                .map(UserEntity::isEnabled)
                .orElseThrow(() -> new NoFoundException("L'utilisateur n'a pas été trouvé"));
    }

    @Override
    public void postUserRegistration(UserRegister user) {
        String hashPassword = hash(user.password());
        RoleEntity roleUser = roleRepository.getRoleEntity("UTILISATEUR");
        if (roleUser == null) {
            throw new NoFoundException("Le rôle n'existe pas !");
        }

        checkMailAndPhone(user.email(), user.phoneNumber());

        String avatarPath = saveAvatar(user.avatar());
        UserEntity userEntity = UserMapper.toEntityForRegistration(user, hashPassword, avatarPath, roleUser);
        jpaRepo.save(userEntity);
    }

    private void checkMailAndPhone(String mail, String phone) {
        if (jpaRepo.existsByMail(mail)) {
            throw new EmailAlreadyUsedException("Le mail est déjà utilisé pour un compte");
        }

        if (jpaRepo.existsByPhoneNumber(phone)) {
            throw new PhoneNumberAlreadyUsedException("Le numéro de téléphone est déjà utilisé pour un compte");
        }
    }

    private String saveAvatar(String avatar) {
        return avatarStorageService.storeUserImage(avatar);
    }

    @Override
    public long count() {
        return jpaRepo.count();
    }

    @Override
    public UserEntity save(UserEntity user) {
        return jpaRepo.save(user);
    }

    private void checkPassword(String password, UserEntity userEntity) {
        if (!PasswordService.verify(userEntity.getPassword(), password)) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }
    }

}
