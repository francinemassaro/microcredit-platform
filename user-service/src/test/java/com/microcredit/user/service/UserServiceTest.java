package com.microcredit.user.service;

import com.microcredit.user.dto.request.CreateUserReqDTO;
import com.microcredit.user.dto.request.UpdateUserReqDTO;
import com.microcredit.user.dto.response.UserResDTO;
import com.microcredit.user.model.User;
import com.microcredit.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void testSaveUser() {
        CreateUserReqDTO request = new CreateUserReqDTO();
        request.setName("Ana");
        request.setEmail("ana@email.com");
        request.setCpf("99999999999");
        request.setPassword("123456");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName(request.getName());
        savedUser.setEmail(request.getEmail());
        savedUser.setCpf(request.getCpf());
        savedUser.setPassword(request.getPassword());
        savedUser.setCreatedAt(LocalDateTime.now());

       when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResDTO response = userService.save(request);

        assertNotNull(response);
        assertEquals("Ana", response.getName());
        assertEquals("ana@email.com", response.getEmail());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    void testSaveWithEmailThatAlreadyExists() {
        CreateUserReqDTO request = new CreateUserReqDTO();
        request.setName("Ana");
        request.setEmail("ana@email.com");
        request.setCpf("99999999999");
        request.setPassword("123456");

        User existingUser = new User();
        existingUser.setEmail(request.getEmail());

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.save(request);
        });

        assertEquals("409 CONFLICT \"E-mail já cadastrado\"", exception.getMessage());
    }

    @Test
    void testGetByIdSuccess() {
        User user = new User();
        user.setId(1L);
        user.setName("Carlos");
        user.setEmail("carlos@email.com");
        user.setPassword("senha");
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResDTO result = userService.getById(1L);

        assertNotNull(result);
        assertEquals("Carlos", result.getName());
    }

    @Test
    void testGetByIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getById(99L);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Ana");
        user1.setEmail("ana@email.com");
        user1.setCpf("11111111111");
        user1.setPassword("senha");
        user1.setCreatedAt(LocalDateTime.now());

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Carlos");
        user2.setEmail("carlos@email.com");
        user2.setCpf("22222222222");
        user2.setPassword("senha123");
        user2.setCreatedAt(LocalDateTime.now());

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserResDTO> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("Ana", result.get(0).getName());
        assertEquals("Carlos", result.get(1).getName());
    }

    @Test
    void testGetAllActiveUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Ana");
        user1.setEmail("ana@email.com");
        user1.setCpf("11111111111");
        user1.setPassword("senha");
        user1.setActive(true);
        user1.setCreatedAt(LocalDateTime.now());

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Carlos");
        user2.setEmail("carlos@email.com");
        user2.setCpf("22222222222");
        user2.setPassword("senha123");
        user2.setActive(true);
        user2.setCreatedAt(LocalDateTime.now());

        when(userRepository.findAllByActiveTrue()).thenReturn(List.of(user1, user2));

        List<UserResDTO> result = userService.getAllActiveUsers();

        assertEquals(2, result.size());
        assertEquals("Ana", result.get(0).getName());
        assertEquals("Carlos", result.get(1).getName());
    }

    @Test
    void testGetAllUsersWithOnlyInactives_ReturnsEmptyList() {
        User user = new User();
        user.setId(1L);
        user.setName("Beatriz");
        user.setEmail("beatriz@email.com");
        user.setCpf("33333333333");
        user.setPassword("senha");
        user.setActive(false);
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.findAllByActiveTrue()).thenReturn(List.of());

        List<UserResDTO> result = userService.getAllUsers();

        assertTrue(result.isEmpty(), "A lista deveria estar vazia pois não há usuários ativos");
    }


    @Test
    void testUpdateUserSuccess() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Ana");
        existingUser.setEmail("ana@email.com");
        existingUser.setCpf("99999999999");
        existingUser.setPassword("senha");

        UpdateUserReqDTO update = new UpdateUserReqDTO();
        update.setName("Ana Maria");
        update.setPassword("novaSenha");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Ana Maria");
        savedUser.setEmail("ana@email.com");
        savedUser.setCpf("99999999999");
        savedUser.setPassword("novaSenha");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResDTO result = userService.updateUser(1L, update);

        assertEquals("Ana Maria", result.getName());
    }

    @Test
    void testUpdateUserNotFound() {
        UpdateUserReqDTO update = new UpdateUserReqDTO();
        update.setName("Novo Nome");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.updateUser(99L, update);
        });

        assertEquals("404 NOT_FOUND \"Usuário não encontrado\"", exception.getMessage());
    }

    @Test
    void testDeletedUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.inactiveUser(99L);
        });

        assertEquals("404 NOT_FOUND \"Usuário não encontrado\"", exception.getMessage());
    }

    @Test
    void testDeletedUserSuccess() {
        User user = new User();
        user.setId(1L);
        user.setActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.inactiveUser(1L);

        assertFalse(user.isActive(), "Usuário deveria estar inativo");
        verify(userRepository).save(user);
    }
}
