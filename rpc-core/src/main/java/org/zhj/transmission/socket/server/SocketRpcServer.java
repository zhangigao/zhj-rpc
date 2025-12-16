package org.zhj.transmission.socket.server;

import lombok.extern.slf4j.Slf4j;
import org.zhj.config.RpcServiceConfig;
import org.zhj.constant.RpcConstant;
import org.zhj.dto.RpcReq;
import org.zhj.dto.RpcResp;
import org.zhj.handler.RpcReqHandler;
import org.zhj.provider.ServiceProvider;
import org.zhj.provider.impl.SimpleServiceProvider;
import org.zhj.transmission.RpcServer;
import org.zhj.util.ShutdownHookUtils;
import org.zhj.util.ThreadPoolUtils;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author 86155
 * @Date 2025/8/26
 */
@Slf4j
public class SocketRpcServer implements RpcServer {

    private final int port;
    private final ServiceProvider serviceProvider;
    private final RpcReqHandler rpcReqHandler;
    private final ExecutorService executorService;

    public SocketRpcServer() {
        this(RpcConstant.SERVER_PORT);
    }

    public SocketRpcServer(ServiceProvider serviceProvider) {
        this(RpcConstant.SERVER_PORT, serviceProvider);
    }

    public SocketRpcServer(int port) {
        this(port, new SimpleServiceProvider());
    }

    public SocketRpcServer(Integer port, ServiceProvider service) {
        this(port, service, ThreadPoolUtils.createIoExecutorService("socket-server-io"));
    }

    public SocketRpcServer(Integer port, ServiceProvider service, ExecutorService executorService) {
        this.port = port;
        this.serviceProvider = service;
        this.rpcReqHandler = new RpcReqHandler(serviceProvider);
        this.executorService = executorService;
    }

    @Override
    public void start() {
        ShutdownHookUtils.clearAll();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                this.executorService.submit(new SocketReqHandler(socket, rpcReqHandler));
            }
        } catch (Exception e) {
            log.error("socket error", e);
        }
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        serviceProvider.publishService(config);
    }
}
