package com.omnia.core.util;

import org.springframework.util.StringUtils;

import java.util.Set;

public class NationalCardUtil {
    private NationalCardUtil() {
    }
    public static boolean nationalCodeValidation(String nationalCode) {
        if (!StringUtils.hasText(nationalCode) || nationalCode.length() != 10) {
            return false;
        }

        Set<String> identicalDigits = Set.of(
                "0000000000", "1111111111", "2222222222", "3333333333", "4444444444",
                "5555555555", "6666666666", "7777777777", "8888888888", "9999999999"
        );

        if (identicalDigits.contains(nationalCode)) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(nationalCode.charAt(i)) * (10 - i);
        }

        int lastDigit = (sum % 11 < 2) ? sum % 11 : 11 - (sum % 11);

        return Character.getNumericValue(nationalCode.charAt(9)) == lastDigit;
    }

}
