package com.zhouzh3.bucket4j.core.annotation;

import com.zhouzh3.bucket4j.core.constants.RateLimitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author haig
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /** 对象类型，如 user、order 等 */
    RateLimitType type();

    /** 唯一标识，比如用户ID、订单ID */
    String key();

    /** 每次调用消耗的令牌数 */
    int tokens() default 1;

}