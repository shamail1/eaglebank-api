package com.eaglebank.api.enums;

public enum TransactionType {
    DEPOSIT("deposit"),
    WITHDRAWAL("withdrawal");
    
    private final String value;
    
    TransactionType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static TransactionType fromString(String value) {
        for (TransactionType type : TransactionType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}

