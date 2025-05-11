package com.microcredit.user.mapper;

import com.microcredit.user.dto.request.CreateUserReqDTO;
import com.microcredit.user.dto.response.UserResDTO;
import com.microcredit.user.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void testMapperToResponseWithMinimalUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Joana");

        UserResDTO dto = UserMapper.toResponse(user);

        assertEquals("Joana", dto.getName());
        assertEquals(1L, dto.getId());
    }

    @Test
    void testMapperToEntityWithMinimalDTO() {
        CreateUserReqDTO dto = new CreateUserReqDTO();
        dto.setName("Bruno");
        dto.setEmail("bruno@email.com");
        dto.setCpf("12345678900");
        dto.setPassword("abc123");

        User user = UserMapper.toEntity(dto);

        assertEquals("Bruno", user.getName());
        assertEquals("12345678900", user.getCpf());
    }
}