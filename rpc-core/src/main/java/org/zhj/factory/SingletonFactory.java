package org.zhj.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SingletonFactory {
    private static final Map<Class<?>, Object> INSTANCES_CACHE = new ConcurrentHashMap<>();


    public static <T> T getInstance(Class<T> clazz) {
        if (clazz == null) {
            throw new RuntimeException("类不能为空");
        }
        Object instance = INSTANCES_CACHE.get(clazz);
        if (instance == null) {
            synchronized (SingletonFactory.class) {
                if (instance == null) {
                    try {
                        instance = clazz.newInstance();
                        INSTANCES_CACHE.put(clazz, instance);
                    } catch (Exception e) {
                        throw new RuntimeException("创建单例实例失败：" + clazz.getName(), e);
                    }
                }
            }
        }
        return clazz.cast(instance);
    }
}
