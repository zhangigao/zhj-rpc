package org.zhj;


import lombok.SneakyThrows;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.nio.charset.StandardCharsets;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @SneakyThrows
    public static void main(String[] args) {
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        zkClient.start();

        // 创建持久化节点（默认就是持久化）
        zkClient.create().forPath("/node1");
        zkClient.create().forPath("/node1/nnn1");
        zkClient.create().withMode(CreateMode.PERSISTENT).forPath("/node1/nnn2");

        // 父节点不能存在创建父节点
        zkClient.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath("/node2/nnn1");

        // 检查节点是否存在
        System.out.println(zkClient.checkExists().forPath("/node1"));

        // 设置节点数据
        zkClient.setData().forPath("/node1", "hello".getBytes());

        // 获取节点数据
        System.out.println(new String(zkClient.getData().forPath("/node1"), StandardCharsets.UTF_8));

        // 删除节点
        zkClient.delete().deletingChildrenIfNeeded().forPath("/node1");
    }
}
