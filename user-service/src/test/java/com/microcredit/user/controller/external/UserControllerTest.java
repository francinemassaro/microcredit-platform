package com.microcredit.user.controller.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microcredit.user.dto.request.CreateUserReqDTO;
import com.microcredit.user.dto.request.UpdateUserReqDTO;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean
    UserService userService;
    @MockBean
    JwtUtil jwtUtil;
    @MockBean
    CustomUserDetailsService customUserDetailsService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testCreateUser() throws Exception {
        CreateUserReqDTO request = new CreateUserReqDTO();
        request.setName("Ana");
        request.setEmail("ana@email.com");
        request.setCpf("12345678900");
        request.setPassword("senha123");

        UserResDTO response = new UserResDTO();
        response.setId(1L);
        response.setName("Ana");
        response.setEmail("ana@email.com");
        response.setCreatedAt(LocalDateTime.now());

        when(userService.save(any(CreateUserReqDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .with(user("admin").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ana"));
    }

    @Test
    void testGetById() throws Exception {
        UserResDTO response = new UserResDTO();
        response.setId(1L);
        response.setName("Carlos");
        response.setEmail("carlos@email.com");
        response.setCreatedAt(LocalDateTime.now());

        when(userService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1")
                        .with(user("admin").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Carlos"));
    }

    @Test
    void testUpdateUser() throws Exception {
        UpdateUserReqDTO request = new UpdateUserReqDTO();
        request.setName("Novo Nome");
        request.setPassword("novaSenha");

        UserResDTO response = new UserResDTO();
        response.setId(1L);
        response.setName("Novo Nome");
        response.setEmail("ana@email.com");
        response.setCreatedAt(LocalDateTime.now());

        when(userService.updateUser(any(Long.class), any(UpdateUserReqDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/api/users/1")
                        .with(user("admin").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Novo Nome"));
    }

    @Test
    void testInactiveUser() throws Exception {
        doNothing().when(userService).inactiveUser(1L);

        mockMvc.perform(delete("/api/users/1")
                        .with(user("admin").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
