package com.eaglebank.api.controller;

import com.eaglebank.api.dto.user.CreateUserRequest;
import com.eaglebank.api.dto.user.UpdateUserRequest;
import com.eaglebank.api.dto.user.UserResponse;
import com.eaglebank.api.service.UserService;
import com.eaglebank.api.util.SecurityContextUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@Validated
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable @Pattern(regexp = "^usr-[A-Za-z0-9]+$") String userId) {
        String authenticatedUserId = SecurityContextUtil.getCurrentUserId();
        UserResponse response = userService.getUserById(userId, authenticatedUserId);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable @Pattern(regexp = "^usr-[A-Za-z0-9]+$") String userId,
            @Valid @RequestBody UpdateUserRequest request) {
        String authenticatedUserId = SecurityContextUtil.getCurrentUserId();
        UserResponse response = userService.updateUser(userId, request, authenticatedUserId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable @Pattern(regexp = "^usr-[A-Za-z0-9]+$") String userId) {
        String authenticatedUserId = SecurityContextUtil.getCurrentUserId();
        userService.deleteUser(userId, authenticatedUserId);
        return ResponseEntity.noContent().build();
    }
}

