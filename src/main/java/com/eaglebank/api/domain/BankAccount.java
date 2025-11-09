package com.eaglebank.api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "bank_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    
    @Id
    @Pattern(regexp = "^01\\d{6}$")
    @Column(name = "account_number", length = 8)
    private String accountNumber;
    
    @NotBlank
    @Column(name = "sort_code", nullable = false, length = 8)
    private String sortCode;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @NotBlank
    @Column(name = "account_type", nullable = false)
    private String accountType;
    
    @NotNull
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "10000.00")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;
    
    @NotBlank
    @Column(nullable = false, length = 3)
    private String currency;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @CreationTimestamp
    @Column(name = "created_timestamp", nullable = false, updatable = false)
    private Instant createdTimestamp;
    
    @UpdateTimestamp
    @Column(name = "updated_timestamp", nullable = false)
    private Instant updatedTimestamp;
}

