package org.zhj.registry.zk;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.zhj.constant.RpcConstant;
import org.zhj.util.IpUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ZkClient {

    // 重试间隔时间
    private static final int BASE_SLEEP_TIME = 1000;
    // 最大重试次数
    private static final int MAX_RETRIES = 3;
    private final CuratorFramework zkClient;
    // /rpc/serviceName: list<ip:port>
    private final Map<String, List<String>> SERVICE_ADDRESS_CACHE = new ConcurrentHashMap<>();
    // /rpc/serviceName/ip:port
    private final Set<String> SERVICE_ADDRESS_SET = ConcurrentHashMap.newKeySet();

    public ZkClient() {
        this(RpcConstant.ZK_HOST, RpcConstant.ZK_PORT);
    }

    public ZkClient(String host, int port) {
        this.zkClient = CuratorFrameworkFactory.builder()
                .connectString(host + StrUtil.COLON + port)
                .retryPolicy(new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES   ))
                .build();
        zkClient.start();
        log.info("zkClient连接成功");
    }

    public void createPersistentNode(String path) {
        if(StrUtil.isBlank(path)){
            throw new IllegalArgumentException("path is null");
        }
        try {
            if (SERVICE_ADDRESS_SET.contains(path)) {
                log.info("节点已存在: {}", path);
                return;
            }
            if (zkClient.checkExists().forPath(path) == null) {
                zkClient.create().creatingParentsIfNeeded().forPath(path);
                SERVICE_ADDRESS_SET.add(path);
                log.info("创建节点成功: {}", path);
            }
        } catch (Exception e) {
            log.error("创建节点失败: {}", path, e);
        }
    }

    public List<String> getChildren(String path) {
        try {
            if(SERVICE_ADDRESS_CACHE.containsKey(path)) {
                log.info("从缓存中获取子节点: {}", path);
                return SERVICE_ADDRESS_CACHE.get(path);
            }
            List<String> ips = zkClient.getChildren().forPath(path);
            log.info("获取子节点成功: {}", path);
            SERVICE_ADDRESS_CACHE.put(path, ips);
            watchNode(path);
            return ips;
        } catch (Exception e) {
            log.error("获取子节点失败: {}", path, e);
            return null;
        }
    }

    private void watchNode(String path) {
        try {
            PathChildrenCache childrenCache = new PathChildrenCache(zkClient, path, true);
            // 注册子节点监听
            PathChildrenCacheListener listener = (client, event) -> {
                List<String> ips = client.getChildren().forPath(path);
                SERVICE_ADDRESS_CACHE.put(path, ips);
                log.info("节点数据更新成功: {}", path);
            };
            childrenCache.getListenable().addListener(listener);
            childrenCache.start();
        } catch (Exception e) {
            log.error("获取子节点失败: {}", path, e);
        }
    }

    public void clearAll(InetSocketAddress address) {
        if(Objects.isNull(address)) {
            throw new IllegalArgumentException("address is null");
        }
        SERVICE_ADDRESS_SET.forEach(path -> {
            if(path.endsWith(IpUtils.toIpPort(address))) {
                try {
                    zkClient.delete().forPath(path);
                    log.info("删除节点成功: {}", path);
                } catch (Exception e) {
                    log.error("删除节点失败: {}", path, e);
                }
            }
        });
    }
}
