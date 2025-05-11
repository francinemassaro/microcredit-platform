package com.microcredit.user.service;

import com.microcredit.user.dto.request.CreateUserReqDTO;
import com.microcredit.user.dto.request.UpdateUserReqDTO;
import com.microcredit.user.dto.response.UserResDTO;
import com.microcredit.user.mapper.UserMapper;
import com.microcredit.user.model.User;
import com.microcredit.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserResDTO save(CreateUserReqDTO request) {
        if(repository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }

        User user = UserMapper.toEntity(request);
        User saved = repository.save(user);
        return UserMapper.toResponse(saved);
    }

    public UserResDTO getById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return UserMapper.toResponse(user);
    }

    public List<UserResDTO> getAllUsers() {
        List<User> users = repository.findAll();
        return users.stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    public UserResDTO updateUser(Long id, UpdateUserReqDTO request) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if(request.getName() != null) {
            user.setName(request.getName());
        }

        if(request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }

        User saved = repository.save(user);
        return UserMapper.toResponse(saved);
    }
}
