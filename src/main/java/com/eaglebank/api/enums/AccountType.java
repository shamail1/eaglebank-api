package com.eaglebank.api.enums;

public enum AccountType {
    PERSONAL("personal");
    
    private final String value;
    
    AccountType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static AccountType fromString(String value) {
        for (AccountType type : AccountType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}

