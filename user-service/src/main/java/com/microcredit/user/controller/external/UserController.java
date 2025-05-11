package com.microcredit.user.controller.external;

import com.microcredit.user.dto.request.CreateUserReqDTO;
import com.microcredit.user.dto.request.UpdateUserReqDTO;
import com.microcredit.user.dto.response.UserResDTO;
import com.microcredit.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResDTO> create(@RequestBody @Valid CreateUserReqDTO request) {
        UserResDTO response = userService.save(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResDTO> getById(@PathVariable Long id) {
        UserResDTO response = userService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResDTO> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserReqDTO request) {

        UserResDTO response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }
}
