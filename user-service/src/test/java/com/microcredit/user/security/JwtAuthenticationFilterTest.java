package com.microcredit.user.security;

import com.microcredit.user.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    @Mock
    JwtUtil jwtUtil;
    @Mock
    CustomUserDetailsService userDetailsService;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_noHeader_shouldChainAndNotAuthenticate() throws Exception {
        var req = mock(HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);

        when(req.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(req, res, chain);

        verify(chain).doFilter(req, res);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_invalidPrefix_shouldChainAndNotAuthenticate() throws Exception {
        var req = mock(HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);

        when(req.getHeader("Authorization")).thenReturn("Token abc");

        filter.doFilterInternal(req, res, chain);

        verify(chain).doFilter(req, res);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_bearerButNoUsername_shouldChainAndNotAuthenticate() throws Exception {
        String token = "abc.token";
        var req = mock(HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);

        when(req.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(null);

        filter.doFilterInternal(req, res, chain);

        verify(chain).doFilter(req, res);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_validBearer_shouldAuthenticateAndChain() throws Exception {
        String token = "valid.jwt.token";
        var req   = mock(HttpServletRequest.class);
        var res   = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);

        when(req.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn("alice");
        var user = User.withUsername("alice").password("x").roles("USER").build();
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(user);
        when(jwtUtil.validateToken(token, user)).thenReturn(true);

        filter.doFilterInternal(req, res, chain);

        verify(chain).doFilter(req, res);
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("alice", auth.getName());

        // aqui extraímos apenas as strings e comparamos listas
        List<String> authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        assertEquals(List.of("ROLE_USER"), authorities);
    }

}
