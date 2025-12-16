package org.zhj.registry.impl;

import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.zhj.constant.RpcConstant;
import org.zhj.factory.SingletonFactory;
import org.zhj.registry.ServiceRegistry;
import org.zhj.registry.zk.ZkClient;
import org.zhj.util.IpUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;

@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {
    private final ZkClient zkClient;

    public ZkServiceRegistry() {
        this(SingletonFactory.getInstance(ZkClient.class));
    }

    public ZkServiceRegistry(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        log.info("注册服务: {}", serviceName);
        zkClient.createPersistentNode(RpcConstant.ZK_REGISTRY_PATH
                        + StrUtil.SLASH
                        + serviceName
                        + StrUtil.SLASH
                        + IpUtils.toIpPort(inetSocketAddress));
        log.info("服务注册成功: {}", serviceName);
    }

    @SneakyThrows
    @Override
    public void clearAll() {
        String host = InetAddress.getLocalHost().getHostAddress();
        int port = RpcConstant.SERVER_PORT;
        zkClient.clearAll(new InetSocketAddress(host, port));
    }
}
