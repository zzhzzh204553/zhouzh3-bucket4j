package com.zhouzh3.bucket4j.core.aspect;

import com.zhouzh3.bucket4j.core.annotation.RateLimit;
import com.zhouzh3.bucket4j.core.constants.RateLimitType;
import com.zhouzh3.bucket4j.core.exception.RateLimitException;
import com.zhouzh3.bucket4j.core.util.SpelResolverUtils;
import com.zhouzh3.bucket4j.service.Bucket4jService;
import io.github.bucket4j.Bucket;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author haig
 */
@Aspect
@Component
public class RateLimitAspect {

    /**管理Bucket的Service*/
    private final Bucket4jService bucketService;

    public RateLimitAspect(Bucket4jService bucketService) {
        this.bucketService = bucketService;
    }


    @Around("@annotation(rateLimit)")
    public Object limit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        String rawKey = rateLimit.key();
        String resolvedKey = SpelResolverUtils.resolveExpression(rawKey, method, joinPoint.getArgs());

        RateLimitType type = rateLimit.type();
        int tokens = rateLimit.tokens();
        Bucket bucket = bucketService.getBucket(type, resolvedKey);

        if (bucket.tryConsume(tokens)) {
            return joinPoint.proceed();
        } else {
            throw new RateLimitException("请求过快！");
        }
    }

}