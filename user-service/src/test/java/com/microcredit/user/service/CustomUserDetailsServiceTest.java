package com.microcredit.user.service;

import com.microcredit.user.model.User;
import com.microcredit.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock UserRepository repository;
    @InjectMocks CustomUserDetailsService service;

    @Test
    void loadUserByUsernameSuccess() {
        User u = new User();
        u.setEmail("x@x.com");
        u.setPassword("pw");
        given(repository.findByEmail("x@x.com")).willReturn(Optional.of(u));

        UserDetails details = service.loadUserByUsername("x@x.com");
        assertEquals("x@x.com", details.getUsername());
        assertEquals("pw", details.getPassword());
    }

    @Test
    void loadUserByUsernameNotFound() {
        given(repository.findByEmail("no@one.com")).willReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("no@one.com"));
    }
}
