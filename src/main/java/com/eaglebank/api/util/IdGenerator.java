package com.eaglebank.api.util;

import com.eaglebank.api.constants.ApplicationConstants;

import java.util.UUID;

public class IdGenerator {
    
    public static String generateUserId() {
        return ApplicationConstants.USER_ID_PREFIX + UUID.randomUUID().toString().replace("-", "");
    }
    
    public static String generateTransactionId() {
        return ApplicationConstants.TRANSACTION_ID_PREFIX + UUID.randomUUID().toString().replace("-", "");
    }
    
    public static String generateAccountNumber() {
        int randomNum = (int) (Math.random() * 1000000);
        return String.format("%s%06d", ApplicationConstants.ACCOUNT_NUMBER_PREFIX, randomNum);
    }
}

