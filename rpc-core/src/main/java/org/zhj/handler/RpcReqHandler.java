package org.zhj.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.shaded.com.google.common.util.concurrent.RateLimiter;
import org.zhj.annotation.Limit;
import org.zhj.dto.RpcReq;
import org.zhj.exception.RpcException;
import org.zhj.provider.ServiceProvider;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RpcReqHandler {

    private final ServiceProvider serviceProvider;
    private static final Map<String, RateLimiter> RATE_LIMITER_MAP = new ConcurrentHashMap<>();

    public RpcReqHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public Object invoke(RpcReq req) {
        Object service = serviceProvider.getService(req.getServiceName());
        try {
            Method method = service.getClass().getMethod(req.getMethodName(), req.getParamTypes());
            Limit limit = method.getAnnotation(Limit.class);
            if (Objects.isNull(limit)) {
                return method.invoke(service, req.getParams());
            }
            RateLimiter rateLimiter = RATE_LIMITER_MAP.computeIfAbsent(req.getMethodName(), __ -> RateLimiter.create(limit.permitsPerSecond()));
            if (!rateLimiter.tryAcquire(limit.timeout(), TimeUnit.MILLISECONDS)) {
                throw new RpcException("当前系统繁忙，请稍后重试");
            }
            return method.invoke(service, req.getParams());
        } catch (Exception e) {
            log.error("invoke error", e);
            throw new RpcException(e.getMessage());
        }
    }
}
