package com.zhouzh3.bucket4j.controller;

import com.zhouzh3.bucket4j.core.constants.RateLimitType;
import com.zhouzh3.bucket4j.service.Bucket4jService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haig
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final Bucket4jService bucket4JService;

    public OrderController(Bucket4jService bucket4jService) {
        this.bucket4JService = bucket4jService;
    }


    @GetMapping("/{userId}")
    public ResponseEntity<String> queryOrder(@PathVariable String userId) {
        // 获取用户的令牌桶
        Bucket bucket = bucket4JService.getBucket(RateLimitType.USER, userId);

        // 尝试消费1个令牌
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            return ResponseEntity.ok()
                    .header(/*剩余可用请求次数*/"X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()))
                    .body("资源访问成功！");
        } else {
            // 计算需要等待的秒数
            long waitSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(/*下次重试等待时间*/"X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitSeconds))
                    .body("请求过多，请 " + waitSeconds + " 秒后重试");
        }
    }
}