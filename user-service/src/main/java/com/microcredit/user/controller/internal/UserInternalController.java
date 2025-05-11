package com.microcredit.user.controller.internal;

import com.microcredit.user.dto.response.UserResDTO;
import com.microcredit.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal")
public class UserInternalController {
    private final UserService service;

    public UserInternalController(UserService service) {
        this.service = service;
    }

    @GetMapping("users/active-inactive")
    public List<UserResDTO> getAllActiveAndInactive(){
        return service.getAllUsers();
    }

    @GetMapping("/users")
    public List<UserResDTO> getAllActiveUsers(){
        return service.getAllActiveUsers();
    }
}
