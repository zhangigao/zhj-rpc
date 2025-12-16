package org.zhj.util;

import lombok.extern.slf4j.Slf4j;
import org.zhj.factory.SingletonFactory;
import org.zhj.registry.impl.ZkServiceRegistry;

@Slf4j
public class ShutdownHookUtils {

    public static void clearAll() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("服务器停止，清空所有资源");
            ZkServiceRegistry registry = SingletonFactory.getInstance(ZkServiceRegistry.class);
            registry.clearAll();
            ThreadPoolUtils.shutdownAll();
        }));
    }
}
