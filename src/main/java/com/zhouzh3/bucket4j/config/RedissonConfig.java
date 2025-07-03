package com.zhouzh3.bucket4j.config;

import cn.hutool.core.util.StrUtil;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.distributed.serialization.Mapper;
import io.github.bucket4j.redis.redisson.Bucket4jRedisson;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.time.Duration.ofSeconds;


/**
 配置模式：
 单节点模式：适用于开发环境或简单应用
 集群模式：适用于Redis Cluster分布式集群
 哨兵模式：适用于高可用生产环境

 重要参数说明：
 connectionPoolSize：最大连接数（根据业务量调整）
 connectionMinimumIdleSize：最小空闲连接数
 connectTimeout：连接超时时间（单位：毫秒）
 timeout：命令等待超时时间
 * @author haig
 */
@Configuration
public class RedissonConfig {


    /**连接池配置*/
    private static final int CONNECTION_POOL_SIZE = 64;
    private static final int CONNECTION_MINIMUM_IDLE_SIZE = 24;
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int SOCKET_TIMEOUT = 3000;
    private static final int REDISSON_THREADS = 16;
    private static final int REDISSON_NETTY_THREADS = 32;


    private final RedisProperties redisProperties;

    public RedissonConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;

    }

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(Config config) {
        return Redisson.create(config);
    }

    @Bean
    public Config config() {
        Config config = new Config();

        // 基础配置
        /*处理Redis事件的线程数*/
        config.setThreads(REDISSON_THREADS)
                /*Netty线程数*/
                .setNettyThreads(REDISSON_NETTY_THREADS)
                .setCodec(new org.redisson.codec.JsonJacksonCodec());

        // 根据模式配置不同连接方式
        if (StrUtil.isNotEmpty(redisProperties.getHost())) {
            config.useSingleServer()
                    .setAddress("redis://" + this.redisProperties.getHost() + ":" + this.redisProperties.getPort())
                    .setPassword(StrUtil.emptyToNull(this.redisProperties.getPassword()))
                    .setConnectTimeout(CONNECT_TIMEOUT)
                    .setTimeout(SOCKET_TIMEOUT)
                    .setConnectionPoolSize(CONNECTION_POOL_SIZE)
                    .setConnectionMinimumIdleSize(CONNECTION_MINIMUM_IDLE_SIZE);
            return config;
        }

        throw new IllegalArgumentException("Unsupported Redis mode: ");
    }

    @Bean
    public ProxyManager<String> proxyManager(RedissonClient redissonClient) {
        return Bucket4jRedisson.casBasedBuilder(((Redisson) redissonClient).getCommandExecutor())
                .expirationAfterWrite(ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(ofSeconds(10)))
                .keyMapper(Mapper.STRING)
                .build();
    }

}
