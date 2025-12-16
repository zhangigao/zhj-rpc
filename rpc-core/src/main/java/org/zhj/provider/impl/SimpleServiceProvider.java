package org.zhj.provider.impl;

import lombok.extern.slf4j.Slf4j;
import org.zhj.config.RpcServiceConfig;
import org.zhj.provider.ServiceProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 86155
 * @Date 2025/8/27
 */
@Slf4j
public class SimpleServiceProvider implements ServiceProvider {

    private final Map<String, Object> SERVICE_CACHE = new ConcurrentHashMap<>();

    @Override
    public void publishService(RpcServiceConfig config) {
        config.rpcServiceNames().forEach(rpcServiceName -> {
            if (SERVICE_CACHE.containsKey(rpcServiceName)) {
                return;
            }
            SERVICE_CACHE.put(rpcServiceName, config.getService());
            log.info("已注册服务：{}", rpcServiceName);
        });

    }

    @Override
    public Object getService(String serviceName) {
        if(!SERVICE_CACHE.containsKey(serviceName)) {
            throw new RuntimeException("未找到服务：" + serviceName);
        }
        return SERVICE_CACHE.get(serviceName);
    }
}
