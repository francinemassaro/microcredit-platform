package com.microcredit.user.service;

import com.microcredit.user.dto.request.CreateUserReqDTO;
import com.microcredit.user.dto.response.UserResDTO;
import com.microcredit.user.mapper.UserMapper;
import com.microcredit.user.model.User;
import com.microcredit.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserResDTO save(CreateUserReqDTO request) {
        User user = UserMapper.toEntity(request);
        User saved = repository.save(user);
        return UserMapper.toResponse(saved);
    }

    public UserResDTO getById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return UserMapper.toResponse(user);
    }
}
