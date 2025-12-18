package org.zhj.transmission.netty.client;


import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author 15566
 * @version 0.0.1
 * @description: TODO
 * @date 2025/12/17 14:58
 */
public class ChannelPool {

    private final Map<String, Channel> pool = new ConcurrentHashMap<>();

    public Channel getChannel(InetSocketAddress addr, Supplier<Channel> supplier) {
        String key = addr.toString();
        Channel channel = pool.get(key);
        if (channel != null && channel.isActive()) {
            return channel;
        }
        return supplier.get();
    }
}
