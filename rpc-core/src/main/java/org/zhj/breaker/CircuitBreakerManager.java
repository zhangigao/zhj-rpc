package org.zhj.breaker;

import org.zhj.annotation.Breaker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 15566
 * @version 0.0.1
 * @description: TODO
 * @date 2025/12/18 16:57
 */
public class CircuitBreakerManager {

    private static final Map<String, CircuitBreaker> BREAKER_MAP = new ConcurrentHashMap<>();

    public static CircuitBreaker get(String key, Breaker breaker) {
        CircuitBreaker circuitBreaker = BREAKER_MAP.computeIfAbsent(key, __ -> new CircuitBreaker(
                breaker.failureThreshold(),
                breaker.successRate(),
                breaker.window()));
        return circuitBreaker;
    }
}
