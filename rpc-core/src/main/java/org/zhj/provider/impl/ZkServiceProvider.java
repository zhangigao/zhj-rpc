package org.zhj.provider.impl;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.zhj.config.RpcServiceConfig;
import org.zhj.constant.RpcConstant;
import org.zhj.factory.SingletonFactory;
import org.zhj.provider.ServiceProvider;
import org.zhj.registry.impl.ZkServiceRegistry;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ZkServiceProvider implements ServiceProvider {

    private final Map<String, Object> SERVICE_CACHE = new ConcurrentHashMap<>();
    private final ZkServiceRegistry zkServiceRegistry;

    public ZkServiceProvider() {
        this(SingletonFactory.getInstance(ZkServiceRegistry.class));
    }

    public ZkServiceProvider(ZkServiceRegistry zkServiceRegistry) {
        this.zkServiceRegistry = zkServiceRegistry;
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        config.rpcServiceNames().forEach(rpcServiceName -> {
            try {
                publishService(rpcServiceName, config.getService());
            } catch (UnknownHostException e) {
                log.error("注册服务失败 {}", rpcServiceName, e);
            }
        });
    }

    @Override
    public Object getService(String serviceName) {
        if (StrUtil.isBlank(serviceName)) {
            throw new RuntimeException("服务名称为空");
        }
        if (!SERVICE_CACHE.containsKey(serviceName)) {
            throw new RuntimeException("未找到服务：" + serviceName);
        }
        return SERVICE_CACHE.get(serviceName);
    }

    public void publishService(String rpcServiceName, Object service) throws UnknownHostException {
        zkServiceRegistry.register(rpcServiceName,
                new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), RpcConstant.SERVER_PORT));
        SERVICE_CACHE.put(rpcServiceName, service);

    }
}
