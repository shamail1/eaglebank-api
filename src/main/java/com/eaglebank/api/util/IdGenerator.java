package com.eaglebank.api.util;

import com.eaglebank.api.constants.ApplicationConstants;

import java.security.SecureRandom;
import java.util.UUID;

public class IdGenerator {
    
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    public static String generateUserId() {
        return ApplicationConstants.USER_ID_PREFIX + UUID.randomUUID().toString().replace("-", "");
    }
    
    public static String generateTransactionId() {
        return ApplicationConstants.TRANSACTION_ID_PREFIX + UUID.randomUUID().toString().replace("-", "");
    }
    
    public static String generateAccountNumber() {
        int randomNum = SECURE_RANDOM.nextInt(1_000_000);
        return String.format("%s%06d", ApplicationConstants.ACCOUNT_NUMBER_PREFIX, randomNum);
    }
}

