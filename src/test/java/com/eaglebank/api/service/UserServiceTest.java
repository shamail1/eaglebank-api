package com.eaglebank.api.service;

import com.eaglebank.api.domain.Address;
import com.eaglebank.api.domain.User;
import com.eaglebank.api.dto.user.AddressDto;
import com.eaglebank.api.dto.user.CreateUserRequest;
import com.eaglebank.api.dto.user.UpdateUserRequest;
import com.eaglebank.api.dto.user.UserResponse;
import com.eaglebank.api.repository.BankAccountRepository;
import com.eaglebank.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Address testAddress;
    private AddressDto testAddressDto;

    @BeforeEach
    void setUp() {
        testAddress = new Address("123 Main St", "Apt 4", null, "London", "Greater London", "SW1A 1AA");
        testAddressDto = new AddressDto("123 Main St", "Apt 4", null, "London", "Greater London", "SW1A 1AA");
        
        testUser = new User();
        testUser.setId("usr-123abc");
        testUser.setName("Test User");
        testUser.setAddress(testAddress);
        testUser.setPhoneNumber("+441234567890");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setCreatedTimestamp(Instant.now());
        testUser.setUpdatedTimestamp(Instant.now());
    }

    @Test
    void createUser_ShouldReturnUserResponse_WhenValidRequest() {
        CreateUserRequest request = new CreateUserRequest(
                "Test User",
                testAddressDto,
                "+441234567890",
                "test@example.com",
                "password123"
        );

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.createUser(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("usr-123abc");
        assertThat(response.name()).isEqualTo("Test User");
        assertThat(response.email()).isEqualTo("test@example.com");
        
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest(
                "Test User",
                testAddressDto,
                "+441234567890",
                "test@example.com",
                "password123"
        );

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_ShouldReturnUserResponse_WhenValidUserId() {
        when(userRepository.findById("usr-123abc")).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserById("usr-123abc", "usr-123abc");

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("usr-123abc");
        assertThat(response.name()).isEqualTo("Test User");
        
        verify(userRepository).findById("usr-123abc");
    }

    @Test
    void getUserById_ShouldThrowForbidden_WhenUserIdDoesNotMatch() {
        assertThatThrownBy(() -> userService.getUserById("usr-123abc", "usr-different"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(userRepository, never()).findById(anyString());
    }

    @Test
    void getUserById_ShouldThrowNotFound_WhenUserDoesNotExist() {
        when(userRepository.findById("usr-123abc")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById("usr-123abc", "usr-123abc"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(userRepository).findById("usr-123abc");
    }

    @Test
    void updateUser_ShouldReturnUpdatedUserResponse_WhenValidRequest() {
        UpdateUserRequest request = new UpdateUserRequest(
                "Updated Name",
                null,
                null,
                null
        );

        when(userRepository.findById("usr-123abc")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.updateUser("usr-123abc", request, "usr-123abc");

        assertThat(response).isNotNull();
        verify(userRepository).findById("usr-123abc");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_ShouldUpdateEmail_WhenEmailIsProvided() {
        UpdateUserRequest request = new UpdateUserRequest(
                null,
                null,
                null,
                "newemail@example.com"
        );

        when(userRepository.findById("usr-123abc")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateUser("usr-123abc", request, "usr-123abc");

        verify(userRepository).existsByEmail("newemail@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenEmailAlreadyExists() {
        UpdateUserRequest request = new UpdateUserRequest(
                null,
                null,
                null,
                "existing@example.com"
        );

        when(userRepository.findById("usr-123abc")).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser("usr-123abc", request, "usr-123abc"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowForbidden_WhenUserIdDoesNotMatch() {
        UpdateUserRequest request = new UpdateUserRequest("New Name", null, null, null);

        assertThatThrownBy(() -> userService.updateUser("usr-123abc", request, "usr-different"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(userRepository, never()).findById(anyString());
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenNoBankAccountsExist() {
        when(userRepository.findById("usr-123abc")).thenReturn(Optional.of(testUser));
        when(bankAccountRepository.existsByUserId("usr-123abc")).thenReturn(false);
        doNothing().when(userRepository).delete(any(User.class));

        userService.deleteUser("usr-123abc", "usr-123abc");

        verify(userRepository).findById("usr-123abc");
        verify(bankAccountRepository).existsByUserId("usr-123abc");
        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteUser_ShouldThrowConflict_WhenBankAccountsExist() {
        when(userRepository.findById("usr-123abc")).thenReturn(Optional.of(testUser));
        when(bankAccountRepository.existsByUserId("usr-123abc")).thenReturn(true);

        assertThatThrownBy(() -> userService.deleteUser("usr-123abc", "usr-123abc"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.CONFLICT);

        verify(userRepository).findById("usr-123abc");
        verify(bankAccountRepository).existsByUserId("usr-123abc");
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void deleteUser_ShouldThrowForbidden_WhenUserIdDoesNotMatch() {
        assertThatThrownBy(() -> userService.deleteUser("usr-123abc", "usr-different"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(userRepository, never()).findById(anyString());
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        User result = userService.findByEmail("test@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void findByEmail_ShouldThrowException_WhenEmailDoesNotExist() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail("nonexistent@example.com"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.UNAUTHORIZED);

        verify(userRepository).findByEmail("nonexistent@example.com");
    }
}

