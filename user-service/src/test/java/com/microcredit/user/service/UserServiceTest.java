package com.microcredit.user.service;

import com.microcredit.user.dto.request.CreateUserReqDTO;
import com.microcredit.user.dto.request.UpdateUserReqDTO;
import com.microcredit.user.dto.response.UserResDTO;
import com.microcredit.user.model.User;
import com.microcredit.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository repository;
    @Mock
    PasswordEncoder encoder;
    @InjectMocks UserService userService;

    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks(this);  // não precisa com @ExtendWith
    }

    @Test
    void testSaveUser() {
        // dado
        CreateUserReqDTO req = new CreateUserReqDTO();
        req.setName("Ana");
        req.setEmail("ana@email.com");
        req.setCpf("99999999999");
        req.setPassword("123456");
        given(repository.findByEmail(req.getEmail())).willReturn(Optional.empty());
        given(encoder.encode("123456")).willReturn("$2a$hash");
        User saved = new User();
        saved.setId(1L);
        saved.setName("Ana");
        saved.setEmail(req.getEmail());
        saved.setCpf(req.getCpf());
        saved.setPassword("$2a$hash");
        saved.setCreatedAt(LocalDateTime.now());
        given(repository.save(any(User.class))).willReturn(saved);

        // quando
        UserResDTO resp = userService.save(req);

        // então
        assertNotNull(resp);
        assertEquals("Ana", resp.getName());
        then(repository).should().save(argThat(u -> u.getPassword().equals("$2a$hash")));
    }

    @Test
    void testSaveWithExistingEmail() {
        CreateUserReqDTO req = new CreateUserReqDTO();
        req.setEmail("x@x.com");
        given(repository.findByEmail("x@x.com"))
                .willReturn(Optional.of(new User()));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.save(req));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertTrue(ex.getReason().contains("E-mail já cadastrado"));
    }

    @Test
    void testGetByIdSuccess() {
        User u = new User();
        u.setId(5L);
        u.setName("Carlos");
        u.setEmail("c@c.com");
        u.setPassword("pw");
        u.setCreatedAt(LocalDateTime.now());
        given(repository.findById(5L)).willReturn(Optional.of(u));

        UserResDTO dto = userService.getById(5L);
        assertEquals("Carlos", dto.getName());
    }

    @Test
    void testGetByIdNotFound() {
        given(repository.findById(99L)).willReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getById(99L));
        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    void testGetAllUsers() {
        User u1 = new User(); u1.setName("A");
        User u2 = new User(); u2.setName("B");
        given(repository.findAll()).willReturn(List.of(u1, u2));

        List<UserResDTO> list = userService.getAllUsers();
        assertEquals(2, list.size());
        assertEquals("A", list.get(0).getName());
    }

    @Test
    void testGetAllActiveUsers() {
        User a = new User(); a.setName("A"); a.setActive(true);
        User b = new User(); b.setName("B"); b.setActive(true);
        given(repository.findAllByActiveTrue()).willReturn(List.of(a, b));

        List<UserResDTO> list = userService.getAllActiveUsers();
        assertEquals(2, list.size());
    }

    @Test
    void testUpdateUserSuccess() {
        User orig = new User(); orig.setId(1L); orig.setName("X"); orig.setPassword("p");
        given(repository.findById(1L)).willReturn(Optional.of(orig));
        given(repository.save(any(User.class))).willAnswer(i -> i.getArgument(0));

        UpdateUserReqDTO req = new UpdateUserReqDTO();
        req.setName("Y");
        req.setPassword("new");
        UserResDTO dto = userService.updateUser(1L, req);

        assertEquals("Y", dto.getName());
    }

    @Test
    void testUpdateUserNotFound() {
        given(repository.findById(50L)).willReturn(Optional.empty());
        UpdateUserReqDTO req = new UpdateUserReqDTO();
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.updateUser(50L, req));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testInactiveUserSuccess() {
        User u = new User(); u.setActive(true);
        given(repository.findById(2L)).willReturn(Optional.of(u));

        userService.inactiveUser(2L);

        assertFalse(u.isActive());
        then(repository).should().save(u);
    }

    @Test
    void testInactiveUserNotFound() {
        given(repository.findById(3L)).willReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.inactiveUser(3L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
