package org.zhj.util;


import org.zhj.factory.SingletonFactory;
import org.zhj.proxy.RpcClientProxy;
import org.zhj.transmission.RpcClient;
import org.zhj.transmission.netty.client.NettyRpcClient;
import org.zhj.transmission.socket.client.SocketRpcClient;

public class ProxyUtils {
    private static final RpcClient rpcClient = SingletonFactory.getInstance(NettyRpcClient.class);
    private static final RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);

    public static <T> T getProxy(Class<T> clazz) {
        return rpcClientProxy.getProxy(clazz);
    }
}
