package com.zhouzh3.bucket4j.service;

import com.zhouzh3.bucket4j.core.constants.RateLimitType;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * @author haig
 */
@Configuration
public class Bucket4jService {

    /**
     *  自动注入Redisson代理*/
    private final ProxyManager<String> proxyManager;

    public Bucket4jService(ProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
    }

    /**
     * 为每个用户创建独立令牌桶
     *
     * @param type
     * @param key
     * @return
     */
    public Bucket getBucket(RateLimitType type, String key) {
        Supplier<BucketConfiguration> supplier = () -> BucketConfiguration.builder()
                .addLimit(getBandwidth(type))
                .build();

        String redisKey = "bucket4j:rate-limit:%s:%s".formatted(type, key);
        return proxyManager.builder().build(redisKey, supplier);
    }

    private Bandwidth getBandwidth(RateLimitType rateLimitType) {
        return switch (rateLimitType) {
            case USER -> Bandwidth.builder().capacity(10)
                    .refillIntervally(10, Duration.ofMinutes(1)).build();
            case ORDER -> Bandwidth.builder().capacity(5)
                    .refillIntervally(5, Duration.ofMinutes(2)).build();
        };
    }

    @Autowired
    private RedissonClient redissonClient;

    @PostConstruct
    public void testRedisConnection() {
        // 尝试连接
        System.out.println("尝试连接================" + redissonClient.getKeys().count());
    }

}