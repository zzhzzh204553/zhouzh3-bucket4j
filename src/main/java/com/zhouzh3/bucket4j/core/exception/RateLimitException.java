package com.zhouzh3.bucket4j.core.exception;

import lombok.Getter;

/**
 * @author haig
 */
@Getter
public class RateLimitException extends RuntimeException {

    private final int code;

    public RateLimitException(String message) {
        super(message);
        this.code = 429;
    }

}
