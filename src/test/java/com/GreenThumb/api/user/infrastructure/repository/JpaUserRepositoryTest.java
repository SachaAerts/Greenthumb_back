package com.GreenThumb.api.user.infrastructure.repository;


import com.GreenThumb.api.user.domain.exception.FormatException;
import com.GreenThumb.api.user.domain.service.PasswordService;
import com.GreenThumb.api.user.infrastructure.mapper.UserMapper;
import com.GreenThumb.api.user.domain.entity.User;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import com.GreenThumb.api.user.infrastructure.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JpaUserRepositoryTest {

    @Mock
    private SpringDataUserRepository jpaRepo;

    @InjectMocks
    private JpaUserRepository jpaUserRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getUsername() - doit retourner le nom de l'utilisateur quand l'id existe")
    void getUsername_shouldReturnUsername_whenUserExists() throws Exception {
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("Joe");
        when(jpaRepo.findById(userId)).thenReturn(Optional.of(userEntity));

        String username = jpaUserRepository.getUsername(userId);

        assertEquals("Joe", username);
        verify(jpaRepo, times(1)).findById(userId);
    }

    @Test
    @DisplayName("getUsername() - doit lancer NoFoundException quand l'utilisateur n'existe pas")
    void getUsername_shouldThrowNoFoundException_whenUserDoesNotExist() {
        Long userId = 42L;
        when(jpaRepo.findById(userId)).thenReturn(Optional.empty());

        NoFoundException exception = assertThrows(NoFoundException.class, () -> jpaUserRepository.getUsername(userId));
        assertTrue(exception.getMessage().contains("L'utilisateur n'a pas été trouvé"));
    }

    @Test
    @DisplayName("getUserByEmail() - doit retourner un User quand l'email existe et que le mapping réussit")
    void getUserByEmail_shouldReturnUser_whenEmailExists() throws Exception {
        String email = "joe@gmail.com";
        String password = "password";

        UserEntity userEntity = new UserEntity();
        userEntity.setMail(email);
        userEntity.setPassword(password);
        userEntity.setEnabled(true);

        User expectedUser = mock(User.class);

        mockStatic(UserMapper.class);
        mockStatic(PasswordService.class);

        when(PasswordService.verify(userEntity.getPassword(), password)).thenReturn(true);
        when(UserMapper.toDomain(userEntity)).thenReturn(expectedUser);
        when(jpaRepo.findByMail(email)).thenReturn(Optional.of(userEntity));

        User user = jpaUserRepository.getUserByEmail(email, password);

        assertEquals(expectedUser, user);
        verify(jpaRepo).findByMail(email);
    }

    @Test
    @DisplayName("getUserByEmail() - doit lancer NoFoundException quand aucun utilisateur n'est trouvé")
    void getUserByEmail_shouldThrowNoFoundException_whenNotFound() {
        String email = "unknown@mail.com";
        String password = "password";
        when(jpaRepo.findByMail(email)).thenReturn(Optional.empty());

        NoFoundException exception = assertThrows(NoFoundException.class, () -> jpaUserRepository.getUserByEmail(email, password));
        assertTrue(exception.getMessage().contains("L'utilisateur n'a pas été trouvé"));
    }

    @Test
    @DisplayName("getUserByEmail() - doit lancer IllegalArgumentException quand le mapping échoue")
    void getUserByEmail_shouldThrowIllegalArgumentException_whenMapperFails() throws FormatException {
        String email = "bad@mail.com";
        String password = "password";

        UserEntity userEntity = new UserEntity();
        userEntity.setMail(email);
        userEntity.setPassword(password);
        userEntity.setEnabled(true);

        mockStatic(UserMapper.class);
        mockStatic(PasswordService.class);

        when(PasswordService.verify(userEntity.getPassword(), password)).thenReturn(true);
        when(UserMapper.toDomain(userEntity)).thenThrow(new FormatException("Format invalide"));
        when(jpaRepo.findByMail(email)).thenReturn(Optional.of(userEntity));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> jpaUserRepository.getUserByEmail(email, password));

        assertTrue(exception.getMessage().contains("Erreur de format interne"));
        verify(jpaRepo).findByMail(email);
    }

    @Test
    @DisplayName("count() - doit retourner le nombre d'utilisateurs")
    void count_shouldReturnUserCount() {
        when(jpaRepo.count()).thenReturn(5L);

        long count = jpaUserRepository.count();

        assertEquals(5L, count);
        verify(jpaRepo).count();
    }

    @AfterEach
    void tearDown() {
        Mockito.framework().clearInlineMocks();
    }
}
