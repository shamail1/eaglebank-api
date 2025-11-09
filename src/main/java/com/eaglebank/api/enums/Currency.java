package com.eaglebank.api.enums;

public enum Currency {
    GBP("GBP");
    
    private final String code;
    
    Currency(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
    
    public static Currency fromString(String code) {
        for (Currency currency : Currency.values()) {
            if (currency.code.equalsIgnoreCase(code)) {
                return currency;
            }
        }
        return null;
    }
}

