package com.leedanbii.board.util;

public class ValidationUtils {

    private ValidationUtils() {}

    public static void validateNotBlank(String... values) {
        for (String value : values) {
            if (value == null || value.trim().isBlank()) {
                throw new IllegalArgumentException("모든 항목을 입력해야 합니다.");
            }
        }
    }
}
