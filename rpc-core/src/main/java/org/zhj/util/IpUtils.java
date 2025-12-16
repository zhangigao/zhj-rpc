package org.zhj.util;

import cn.hutool.core.util.StrUtil;

import java.net.InetSocketAddress;
import java.util.Objects;

public class IpUtils {


    public static String toIpPort(InetSocketAddress address) {
        if(address == null) {
            throw new IllegalArgumentException("address is null");
        }
        String host = address.getHostString();
        if(Objects.equals("localhost", host)) {
            host = "127.0.0.1";
        }
        return host + StrUtil.COLON + address.getPort();
    }

    public static InetSocketAddress toInetSocketAddress(String address) {
        if(StrUtil.isBlank(address)) {
            throw new IllegalArgumentException("address is null");
        }
        String[] split = address.split(StrUtil.COLON);
        if(split.length != 2) {
            throw new IllegalArgumentException("address is invalid");
        }
        String host = split[0];
        if(Objects.equals("localhost", host)) {
            host = "127.0.0.1";
        }
        int port = Integer.parseInt(split[1]);
        if(port <= 0 || port > 65535) {
            throw new IllegalArgumentException("port is invalid");
        }
        return new InetSocketAddress(host, port);
    }
}
