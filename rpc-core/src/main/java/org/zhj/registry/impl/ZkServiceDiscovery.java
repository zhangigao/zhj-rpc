package org.zhj.registry.impl;

import cn.hutool.core.util.StrUtil;
import org.zhj.constant.RpcConstant;
import org.zhj.dto.RpcReq;
import org.zhj.factory.SingletonFactory;
import org.zhj.loadbalance.LoadBalance;
import org.zhj.loadbalance.impl.RandomLoadBalance;
import org.zhj.registry.ServiceDiscovery;
import org.zhj.registry.zk.ZkClient;
import org.zhj.util.IpUtils;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ZkServiceDiscovery implements ServiceDiscovery {
    private final ZkClient zkClient;
    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        this(SingletonFactory.getInstance(ZkClient.class));
    }

    public ZkServiceDiscovery(ZkClient zkClient) {
        this(zkClient, SingletonFactory.getInstance(RandomLoadBalance.class));
    }

    public ZkServiceDiscovery(ZkClient zkClient, LoadBalance loadBalance) {
        this.zkClient = zkClient;
        this.loadBalance = loadBalance;
    }

    @Override
    public InetSocketAddress lookupService(RpcReq req) {
        List<String> serviceAddresses = zkClient.getChildren(RpcConstant.ZK_REGISTRY_PATH
                + StrUtil.SLASH
                + req.getServiceName());
        String address = loadBalance.select(serviceAddresses);
        return IpUtils.toInetSocketAddress(address);

    }
}
