package com.eaglebank.api.service;

import com.eaglebank.api.domain.Address;
import com.eaglebank.api.domain.User;
import com.eaglebank.api.dto.user.CreateUserRequest;
import com.eaglebank.api.dto.user.UpdateUserRequest;
import com.eaglebank.api.dto.user.UserResponse;
import com.eaglebank.api.repository.BankAccountRepository;
import com.eaglebank.api.repository.UserRepository;
import com.eaglebank.api.util.EntityMapper;
import com.eaglebank.api.util.IdGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, 
                      BankAccountRepository bankAccountRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }
        
        User user = new User();
        user.setId(IdGenerator.generateUserId());
        user.setName(request.name());
        user.setAddress(EntityMapper.toAddress(request.address()));
        user.setPhoneNumber(request.phoneNumber());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        
        User savedUser = userRepository.save(user);
        return EntityMapper.toUserResponse(savedUser);
    }
    
    public UserResponse getUserById(String userId, String authenticatedUserId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!userId.equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return EntityMapper.toUserResponse(user);
    }
    
    public UserResponse updateUser(String userId, UpdateUserRequest request, String authenticatedUserId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!userId.equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (request.name() != null) {
            user.setName(request.name());
        }
        if (request.address() != null) {
            user.setAddress(EntityMapper.toAddress(request.address()));
        }
        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber());
        }
        if (request.email() != null) {
            if (userRepository.existsByEmail(request.email()) && !user.getEmail().equals(request.email())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
            }
            user.setEmail(request.email());
        }
        
        User updatedUser = userRepository.save(user);
        return EntityMapper.toUserResponse(updatedUser);
    }
    
    public void deleteUser(String userId, String authenticatedUserId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!userId.equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (bankAccountRepository.existsByUserId(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "A user cannot be deleted when they are associated with a bank account");
        }
        
        userRepository.delete(user);
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    }
}

