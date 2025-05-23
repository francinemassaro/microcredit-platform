package com.microcredit.user.mapper;

import com.microcredit.user.dto.request.CreateUserReqDTO;
import com.microcredit.user.dto.response.UserResDTO;
import com.microcredit.user.model.User;

public class UserMapper {

    public static User toEntity(CreateUserReqDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setCpf(dto.getCpf());
        user.setPassword(dto.getPassword());
        return user;
    }

    public static UserResDTO toResponse(User user) {
        UserResDTO userResDTO = new UserResDTO();
        userResDTO.setId(user.getId());
        userResDTO.setName(user.getName());
        userResDTO.setEmail(user.getEmail());
        userResDTO.setCreatedAt(user.getCreatedAt());
        userResDTO.setActive(user.isActive());
        return userResDTO;
    }
}
