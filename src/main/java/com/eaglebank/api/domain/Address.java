package com.eaglebank.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    
    @NotBlank
    @Column(nullable = false)
    private String line1;
    
    @Column
    private String line2;
    
    @Column
    private String line3;
    
    @NotBlank
    @Column(nullable = false)
    private String town;
    
    @NotBlank
    @Column(nullable = false)
    private String county;
    
    @NotBlank
    @Column(nullable = false)
    private String postcode;
}

