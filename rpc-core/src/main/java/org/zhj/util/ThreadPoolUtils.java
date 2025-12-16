package org.zhj.util;

import cn.hutool.core.thread.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class ThreadPoolUtils {

    private static final Map<String, ExecutorService> THREAD_POOL_MAP = new ConcurrentHashMap<>();
    private static final int DEFAULT_KEEP_ALIVE_TIME = 60;
    private static final int DEFAULT_QUEUE_SIZE = 1024;
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final int CPU_INTENSIVE_THREAD_COUNT = AVAILABLE_PROCESSORS + 1;
    private static final int IO_INTENSIVE_THREAD_COUNT = AVAILABLE_PROCESSORS * 2;


    public static ExecutorService createIoExecutorService(String poolName) {
        return createExecutorService(IO_INTENSIVE_THREAD_COUNT, poolName);
    }

    public static ExecutorService createCpuExecutorService(String poolName) {
        return createExecutorService(CPU_INTENSIVE_THREAD_COUNT, poolName);
    }

    public static ExecutorService createExecutorService(int coreSize, String poolName) {
        return createExecutorService(coreSize, coreSize, poolName);
    }

    public static ExecutorService createExecutorService(int coreSize, int maxSize, String poolName) {
        return createExecutorService(coreSize, maxSize, DEFAULT_KEEP_ALIVE_TIME, DEFAULT_QUEUE_SIZE, poolName);
    }

    public static ExecutorService createExecutorService(int coreSize, int maxSize, long keepAliveTime, int queueSize, String poolName) {
        return createExecutorService(coreSize, maxSize, keepAliveTime, queueSize, poolName, false);
    }

    public static ExecutorService createExecutorService(int coreSize, int maxSize, long keepAliveTime, int queueSize, String poolName, boolean isDaemon) {
        if (THREAD_POOL_MAP.containsKey(poolName)) {
            return THREAD_POOL_MAP.get(poolName);
        }
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                coreSize,
                maxSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueSize),
                new NamedThreadFactory(poolName, isDaemon)
        );
        THREAD_POOL_MAP.put(poolName, executor);
        log.info("创建线程池：{}", poolName);
        return executor;
    }

    public static void shutdown(String poolName) {
        // 从map中取出线程池
        ExecutorService executorService = THREAD_POOL_MAP.remove(poolName); // 先remove再使用
        if (executorService != null) {
            // try-with-resources自动调用executorService.close()（即shutdown()）
            try (executorService) {
                log.info("关闭线程池: {}", poolName);
            }
        }
    }

    public static void shutdownAll() {
        // 并发关闭线程池
        THREAD_POOL_MAP.entrySet().parallelStream()
                .forEach(entry -> {
                    String poolName = entry.getKey();
                    log.info("关闭线程池：{}", poolName);
                    ExecutorService executor = entry.getValue();
                    executor.shutdown();
                    try {
                        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                            executor.shutdownNow();
                            log.warn("线程池：{} 未关闭，已强制关闭", poolName);
                        }
                    } catch (InterruptedException e) {
                        executor.shutdownNow();
                        log.warn("线程池：{} 未关闭，已强制关闭", poolName);
                    }
                });
        THREAD_POOL_MAP.clear();
    }
}
