package com.GreenThumb.api.user.infrastructure.repository;

import com.GreenThumb.api.user.application.dto.PageResponse;
import com.GreenThumb.api.user.application.dto.AdminUserDto;
import com.GreenThumb.api.user.application.dto.Passwords;
import com.GreenThumb.api.user.application.dto.UserEdit;
import com.GreenThumb.api.user.application.dto.UserRegister;
import com.GreenThumb.api.user.application.dto.UserSearchFilters;
import com.GreenThumb.api.user.domain.entity.User;
import com.GreenThumb.api.user.domain.exception.*;
import com.GreenThumb.api.user.domain.repository.RoleRepository;
import com.GreenThumb.api.user.domain.repository.UserRepository;
import com.GreenThumb.api.user.domain.service.AvatarStorageService;
import com.GreenThumb.api.user.domain.service.PasswordService;
import com.GreenThumb.api.user.infrastructure.entity.RoleEntity;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import com.GreenThumb.api.user.infrastructure.mapper.UserMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static com.GreenThumb.api.user.domain.service.PasswordService.hash;

@Slf4j
@Repository
public class JpaUserRepository implements UserRepository {
    private final SpringDataUserRepository jpaRepo;
    private final RoleRepository roleRepository;
    private final AvatarStorageService avatarStorageService;
    private final String deletedAvatarUrl;

    @PersistenceContext
    private EntityManager entityManager;

    public JpaUserRepository(SpringDataUserRepository jpaRepo, RoleRepository roleRepository, AvatarStorageService avatarStorageService, @Value("${greenthumb.avatar.default-url}") String deletedAvatarUrl) {
        this.jpaRepo = jpaRepo;
        this.roleRepository = roleRepository;
        this.avatarStorageService = avatarStorageService;
        this.deletedAvatarUrl = deletedAvatarUrl;
    }

    private String sanitizeSearchQuery(String query) {
        if (query == null) {
            return "";
        }
        return query.trim()
                .replaceAll("[&|!():<>*]", " ")
                .replaceAll("\\s+", " ")
                .trim();
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
                    if (userEntity.getDeletedAt() != null) {
                        log.warn("Tentative de connexion avec un compte désactivé: {}", email);
                        throw new IllegalStateException("Ce compte a été désactivé définitivement");
                    }

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
                    if (userEntity.getDeletedAt() != null) {
                        log.warn("Tentative de connexion avec un compte désactivé: {}", username);
                        throw new IllegalStateException("Ce compte a été désactivé définitivement");
                    }

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

        checkMailAndPhone(user.email(), user.phoneNumber());
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

    @Override
    public boolean existUser(String email) {
        return jpaRepo.existsByMail(email);
    }

    private void checkMailAndPhone(String mail, String phone) {
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

        if (!oldUsername.equals(userEdit.username()) && !userEdit.email().equals(user.getMail()) && !userEdit.phoneNumber().equals(user.getPhoneNumber())) {
            checkMailAndPhone(userEdit.email(), userEdit.phoneNumber());
            checkUsername(userEdit.username());
        }

        editUser(userEdit, user);

        jpaRepo.save(user);
    }

    private void editUser(UserEdit userEdit, UserEntity user) {
        if (userEdit.username() != null) user.setUsername(userEdit.username());
        if (userEdit.firstname() != null) user.setFirstname(userEdit.firstname());
        if (userEdit.lastname() != null) user.setLastname(userEdit.lastname());
        if (userEdit.email() != null) user.setMail(userEdit.email());
        if (userEdit.phoneNumber() != null) user.setPhoneNumber(userEdit.phoneNumber());
        if (userEdit.biography() != null) user.setBiography(userEdit.biography());

        if (userEdit.avatar() != null) {
            String newAvatar = avatarStorageService.replaceUserImage(
                    user.getAvatar(),
                    userEdit.avatar()
            );
            user.setAvatar(newAvatar);
        }
    }

    @Override
    @Transactional
    public void editPassword(Passwords passwords, String oldUsername) {
        UserEntity user = jpaRepo.findByUsername(oldUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        editPassword(passwords, user);

        jpaRepo.save(user);
    }

    @Override
    @Transactional
    public void editPasswordByMail(Passwords passwords, String email) {
        UserEntity user = jpaRepo.findByMail(email)
                .orElseThrow(() -> new NoFoundException("User not found"));

        editPassword(passwords, user);

        jpaRepo.save(user);
    }

    private void editPassword(Passwords passwords, UserEntity user) {
        String hashPassword = hash(passwords.password());
        if(hashPassword != null) {user.setPassword(hashPassword);}
    }

    @Override
    public AdminUserDto findByUsernameForAdmin(String username) {
        return jpaRepo.findByUsername(username)
                .map(AdminUserDto::fromEntity)
                .orElseThrow(() -> new NoFoundException("L'utilisateur n'a pas été trouvé"));
    }

    @Override
    @Transactional
    public void setUserEnabled(String username, boolean enabled) {
        int updated = jpaRepo.updateEnabledByUsername(username, enabled);
        if (updated == 0) {
            throw new NoFoundException("L'utilisateur n'a pas été trouvé");
        }
    }

    @Override
    @Transactional
    public void deactivateUserByUsername(String username) {
        UserEntity user = jpaRepo.findByUsername(username)
                .orElseThrow(() -> new NoFoundException("L'utilisateur n'a pas été trouvé"));

        if (user.getDeletedAt() != null) {
            log.warn("Tentative de désactivation d'un compte déjà désactivé: {}", username);
            throw new IllegalStateException("Ce compte est déjà désactivé");
        }

        Long userId = user.getId();
        String originalUsername = user.getUsername();
        
        user.setUsername("deleted_user_" + userId);
        user.setFirstname("Utilisateur");
        user.setLastname("Désactivé");
        user.setMail("deleted_" + userId + "@greenthumb.local");
        user.setPhoneNumber(null);
        user.setBiography(null);
        
        user.setAvatar(this.deletedAvatarUrl);
        
        user.setDeletedAt(LocalDateTime.now());

        jpaRepo.save(user);

        log.info("User '{}' (ID: {}) deactivated and anonymized. New username: '{}'",
                originalUsername, userId, user.getUsername());
    }

    @Override
    @Transactional
    public void hardDeleteUserByUsername(String username) {
        if (!jpaRepo.existsByUsername(username)) {
            throw new NoFoundException("L'utilisateur n'a pas été trouvé");
        }
        jpaRepo.deleteByUsername(username);
    }

    @Override
    public boolean isAdmin(String username) {
        String role = jpaRepo.findRoleByUsername(username);
        return role != null && role.equalsIgnoreCase("ADMIN");
    }

    @Override
    @SuppressWarnings("unchecked")
    public PageResponse<AdminUserDto> searchUsers(UserSearchFilters filters, int page, int size) {
        int offset = page * size;

        StringBuilder baseQuery = new StringBuilder();
        StringBuilder countQuery = new StringBuilder();
        List<Object> params = new ArrayList<>();
        int paramIndex = 1;

        baseQuery.append("SELECT u.* FROM users u ");
        baseQuery.append("LEFT JOIN roles r ON u.id_role = r.id_role ");
        countQuery.append("SELECT COUNT(*) FROM users u ");
        countQuery.append("LEFT JOIN roles r ON u.id_role = r.id_role ");

        StringBuilder whereClause = new StringBuilder();
        List<String> conditions = new ArrayList<>();

        if (filters.hasQuery()) {
            String sanitizedQuery = sanitizeSearchQuery(filters.query());
            conditions.add("to_tsvector('french', coalesce(u.username, '') || ' ' || coalesce(u.mail, '') || ' ' || coalesce(u.firstname, '') || ' ' || coalesce(u.lastname, '')) @@ plainto_tsquery('french', ?" + paramIndex + ")");
            params.add(sanitizedQuery);
            paramIndex++;
        }

        if (filters.isActiveOnly()) {
            conditions.add("u.deleted_at IS NULL");
        } else if (filters.isDeletedOnly()) {
            conditions.add("u.deleted_at IS NOT NULL");
        }

        if (filters.hasEnabledFilter()) {
            conditions.add("u.enabled = ?" + paramIndex);
            params.add(filters.enabled());
            paramIndex++;
        }

        if (filters.hasRoleFilter()) {
            conditions.add("UPPER(r.label) = UPPER(?" + paramIndex + ")");
            params.add(filters.role());
            paramIndex++;
        }

        if (!conditions.isEmpty()) {
            whereClause.append("WHERE ").append(String.join(" AND ", conditions)).append(" ");
        }

        baseQuery.append(whereClause);
        countQuery.append(whereClause);

        if (filters.hasQuery()) {
            String sanitizedQuery = sanitizeSearchQuery(filters.query());
            baseQuery.append("ORDER BY ts_rank(to_tsvector('french', coalesce(u.username, '') || ' ' || coalesce(u.mail, '') || ' ' || coalesce(u.firstname, '') || ' ' || coalesce(u.lastname, '')), plainto_tsquery('french', ?").append(paramIndex).append(")) DESC ");
            params.add(sanitizedQuery);
        } else {
            baseQuery.append("ORDER BY u.id_user DESC ");
        }

        baseQuery.append("LIMIT ").append(size).append(" OFFSET ").append(offset);

        Query nativeQuery = entityManager.createNativeQuery(baseQuery.toString(), UserEntity.class);
        Query nativeCountQuery = entityManager.createNativeQuery(countQuery.toString());

        for (int i = 0; i < params.size(); i++) {
            nativeQuery.setParameter(i + 1, params.get(i));
            if (i < params.size() - (filters.hasQuery() ? 1 : 0)) {
                nativeCountQuery.setParameter(i + 1, params.get(i));
            }
        }

        List<UserEntity> users = nativeQuery.getResultList();
        long totalElements = ((Number) nativeCountQuery.getSingleResult()).longValue();

        List<AdminUserDto> content = users.stream()
                .map(AdminUserDto::fromEntity)
                .toList();

        return PageResponse.of(content, totalElements, page, size);
    }

    @Override
    @Transactional
    public void incrementTasksCompleted(Long userId) {
        UserEntity user = jpaRepo.findById(userId)
                .orElseThrow(() -> new NoFoundException("L'utilisateur n'a pas été trouvé"));
        user.setTasksCompleted(user.getTasksCompleted() + 1);
        jpaRepo.save(user);
    }
}
