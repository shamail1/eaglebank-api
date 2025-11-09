package com.eaglebank.api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @Column(name = "id", length = 50)
    private String id;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @Embedded
    private Address address;
    
    @NotBlank
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$")
    @Column(nullable = false)
    private String phoneNumber;
    
    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;
    
    @NotBlank
    @Column(nullable = false)
    private String password;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdTimestamp;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedTimestamp;
}

