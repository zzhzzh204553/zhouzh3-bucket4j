package com.zhouzh3.bucket4j.core.constants;

import lombok.Getter;

/**
 * @author haig
 */

public enum RateLimitType {
    /***/
    USER("user"),

    ORDER("order");

    @Getter
    private final String value;

    RateLimitType(String value) {
        this.value = value;
    }


}
