package com.GreenThumb.api.user.infrastructure.repository;

import com.GreenThumb.api.user.application.dto.Passwords;
import com.GreenThumb.api.user.application.dto.UserEdit;
import com.GreenThumb.api.user.application.dto.UserRegister;
import com.GreenThumb.api.user.domain.entity.User;
import com.GreenThumb.api.user.domain.exception.*;
import com.GreenThumb.api.user.domain.repository.RoleRepository;
import com.GreenThumb.api.user.domain.repository.UserRepository;
import com.GreenThumb.api.user.domain.service.AvatarStorageService;
import com.GreenThumb.api.user.domain.service.PasswordService;
import com.GreenThumb.api.user.infrastructure.entity.RoleEntity;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import com.GreenThumb.api.user.infrastructure.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
                    if (!userEntity.isEnabled()) {
                        log.warn("Tentative de connexion avec un compte non vérifié: {}", email);
                        throw new AccountNotVerifiedException(
                                "Votre compte n'est pas encore vérifié. Veuillez vérifier votre email."
                        );
                    }

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
                    if (!userEntity.isEnabled()) {
                        log.warn("Tentative de connexion avec un compte non vérifié: {}", username);
                        throw new AccountNotVerifiedException(
                                "Votre compte n'est pas encore vérifié. Veuillez vérifier votre email."
                        );
                    }

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
    @Transactional
    public void postUserRegistration(UserRegister user) {
        String hashPassword = hash(user.password());
        RoleEntity roleUser = roleRepository.getRoleEntity("UTILISATEUR");
        if (roleUser == null) {
            throw new NoFoundException("Le rôle n'existe pas !");
        }

        checkMailPhone(user.email(), user.phoneNumber());
        checkUsername(user.username());

        String avatarPath = saveAvatar(user.avatar());
        UserEntity userEntity = UserMapper.toEntityForRegistration(user, hashPassword, avatarPath, roleUser);
        jpaRepo.save(userEntity);
    }

    @Override
    public long count() {
        return jpaRepo.count();
    }

    @Override
    public long getIdByUsername(String username) {
        return jpaRepo.findIdByUsername(username);
    }

    private void checkMailPhone(String mail, String phone) {
        if (jpaRepo.existsByMail(mail)) {
            throw new EmailAlreadyUsedException("Le mail est déjà utilisé pour un compte");
        }

        if (jpaRepo.existsByPhoneNumber(phone)) {
            throw new PhoneNumberAlreadyUsedException("Le numéro de téléphone est déjà utilisé pour un compte");
        }
    }

    private void checkUsername(String username) {
        if (jpaRepo.existsByUsername(username)) {
            throw new UsernameAlreadyUsedException("Le nom d'utilisateur est déjà pris");
        }
    }

    private String saveAvatar(String avatar) {
        return avatarStorageService.storeUserImage(avatar);
    }

    private void checkPassword(String password, UserEntity userEntity) {
        if (!PasswordService.verify(userEntity.getPassword(), password)) {
            throw new IllegalArgumentException("Mot de passe incorrect");
        }
    }

    @Override
    @Transactional
    public void editUser(UserEdit userEdit, String oldUsername) {
        UserEntity user = jpaRepo.findByUsername(oldUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        checkMailPhone(userEdit.email(), userEdit.phoneNumber());
        if (!oldUsername.equals(userEdit.username())) {
            checkUsername(userEdit.username());
        }

        editUser(userEdit, user);

        jpaRepo.save(user);
    }

    private void editUser(UserEdit userEdit, UserEntity user) {
        if (userEdit.username() != null) {user.setUsername(userEdit.username());}
        if (userEdit.firstname() != null) {user.setFirstname(userEdit.firstname());}
        if (userEdit.lastname() != null) {user.setLastname(userEdit.lastname());}
        if (userEdit.email() != null) {user.setMail(userEdit.email());}
        if (userEdit.phoneNumber() != null) {user.setPhoneNumber(userEdit.phoneNumber());}
        if (userEdit.biography() != null) {user.setBiography(userEdit.biography());}

        AvatarStorageService avatarStorageService = new AvatarStorageService();
        String newAvatar = avatarStorageService.replaceUserImage(user.getAvatar(), userEdit.avatar());
        if (newAvatar != null) {user.setAvatar(newAvatar);}
    }

    @Override
    @Transactional
    public void editPassword(Passwords passwords, String oldUsername) {
        UserEntity user = jpaRepo.findByUsername(oldUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        editPassword(passwords, user);

        jpaRepo.save(user);
    }

    private void editPassword(Passwords passwords, UserEntity user) {
        String hashPassword = hash(passwords.password());
        if(hashPassword != null) {user.setPassword(passwords.password());}
    }
}
