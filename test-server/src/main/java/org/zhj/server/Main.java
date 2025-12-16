package org.zhj.server;

import lombok.extern.slf4j.Slf4j;
import org.zhj.config.RpcServiceConfig;
import org.zhj.factory.SingletonFactory;
import org.zhj.provider.impl.ZkServiceProvider;
import org.zhj.server.service.UserServiceImpl;
import org.zhj.transmission.RpcServer;
import org.zhj.transmission.netty.server.NettyRpcServer;
import org.zhj.transmission.socket.server.SocketRpcServer;

/**
 * @Author 86155
 * @Date 2025/8/25
 */
@Slf4j
public class Main {
    public static void main(String[] args) {
        try {
            RpcServer rpcServer = new NettyRpcServer();
            rpcServer.publishService(new RpcServiceConfig(new UserServiceImpl()));
            rpcServer.start();
        } catch (Exception e) {
            log.error("server error", e);
        }
    }
}
