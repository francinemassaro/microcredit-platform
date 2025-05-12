package com.microcredit.user.controller.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microcredit.user.dto.response.UserResDTO;
import com.microcredit.user.security.JwtUtil;
import com.microcredit.user.service.CustomUserDetailsService;
import com.microcredit.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserInternalController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserInternalControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;
    @MockBean
    JwtUtil jwtUtil;
    @MockBean
    CustomUserDetailsService customUserDetailsService;

    @Test
    void testGetAllActiveUsers() throws Exception {
        UserResDTO user = new UserResDTO();
        user.setId(1L);
        user.setName("Ana");
        user.setEmail("ana@email.com");
        user.setCreatedAt(LocalDateTime.now());

        when(userService.getAllActiveUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/internal/users")
                        .with(user("admin").roles("USER"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ana"))
                .andExpect(jsonPath("$[0].email").value("ana@email.com"));
    }

    @Test
    void testGetAllUsersIncludingInactive() throws Exception {
        UserResDTO user = new UserResDTO();
        user.setId(2L);
        user.setName("Carlos");
        user.setEmail("carlos@email.com");
        user.setCreatedAt(LocalDateTime.now());

        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/internal/users/active-inactive")
                        .with(user("admin").roles("USER"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Carlos"))
                .andExpect(jsonPath("$[0].email").value("carlos@email.com"));
    }
}