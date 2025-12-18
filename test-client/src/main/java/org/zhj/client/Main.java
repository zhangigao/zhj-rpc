package org.zhj.client;

import lombok.extern.slf4j.Slf4j;
import org.zhj.api.UserService;
import org.zhj.dto.RpcReq;
import org.zhj.dto.RpcResp;
import org.zhj.entity.User;
import org.zhj.proxy.RpcClientProxy;
import org.zhj.transmission.RpcClient;
import org.zhj.transmission.netty.client.NettyRpcClient;
import org.zhj.transmission.socket.client.SocketRpcClient;
import org.zhj.util.ProxyUtils;
import org.zhj.util.ThreadPoolUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author 86155
 * @Date 2025/8/25
 */
@Slf4j
public class Main {
    public static void main(String[] args) {
        UserService userService = ProxyUtils.getProxy(UserService.class);
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 20; i++) {
            executorService.execute(() -> {
                User user = userService.getUser(1L);
                System.out.println(user);
            });
        }
    }
}
