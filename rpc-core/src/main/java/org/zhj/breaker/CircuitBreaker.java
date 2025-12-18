package org.zhj.breaker;

import org.zhj.enums.State;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 15566
 * @version 0.0.1
 * @description: TODO
 * @date 2025/12/18 16:16
 */
public class CircuitBreaker {

    private State state = State.CLOSE;
    private final AtomicInteger total = new AtomicInteger(0);
    private final AtomicInteger success = new AtomicInteger(0);
    private final AtomicInteger failure = new AtomicInteger(0);
    // 熔断阈值
    private final int failureThreshold;
    // 恢复阈值
    private final double successRate;
    private final long window;
    private long lastTime;

    public CircuitBreaker(int failureThreshold, double successRate, long window) {
        this.failureThreshold = failureThreshold;
        this.successRate = successRate;
        this.window = window;
    }

    public synchronized boolean canReq() {
        switch (state) {
            case CLOSE:
                return true;
            case OPEN:
                if (System.currentTimeMillis() - lastTime > window) {
                    this.state = State.HALF_OPEN;
                    reset();
                    return true;
                }
                return false;
            case HALF_OPEN:
                total.incrementAndGet();
                return true;
            default:
                throw new IllegalStateException("熔断器状态异常");
        }
    }

    public synchronized void success() {
        if (state == State.CLOSE) {
            reset();
            return;
        }
        success.incrementAndGet();
        if (success.get() >= successRate * total.get()) {
            state = State.CLOSE;
            reset();
        }
    }

    public synchronized void failure() {
        lastTime = System.currentTimeMillis();
        if (state == State.HALF_OPEN) {
            state = State.OPEN;
            reset();
            return;
        }
        if (state == State.CLOSE) {
            failure.incrementAndGet();
            if (failure.get() >= failureThreshold) {
                state = State.OPEN;
                reset();
            }
        }
    }

    public void reset() {
        failure.set(0);
        total.set(0);
        success.set(0);
    }
}
