package com.microcredit.user.service;

import com.microcredit.user.dto.request.CreateUserReqDTO;
import com.microcredit.user.dto.response.UserResDTO;
import com.microcredit.user.model.User;
import com.microcredit.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
        request.setPassword("123456");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName(request.getName());
        savedUser.setEmail(request.getEmail());
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
}
