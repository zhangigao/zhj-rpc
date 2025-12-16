package org.zhj.transmission.socket.client;

import lombok.extern.slf4j.Slf4j;
import org.zhj.dto.RpcReq;
import org.zhj.dto.RpcResp;
import org.zhj.factory.SingletonFactory;
import org.zhj.registry.ServiceDiscovery;
import org.zhj.registry.impl.ZkServiceDiscovery;
import org.zhj.transmission.RpcClient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Author 86155
 * @Date 2025/8/26
 */
@Slf4j
public class SocketRpcClient implements RpcClient {
    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        this(SingletonFactory.getInstance(ZkServiceDiscovery.class));
    }

    public SocketRpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @Override
    public RpcResp<?> sendReq(RpcReq req) {
        InetSocketAddress address = serviceDiscovery.lookupService(req);
        try(Socket socket = new Socket(address.getAddress(), address.getPort());) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(req);
            objectOutputStream.flush();
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            return (RpcResp<?>) objectInputStream.readObject();
        } catch (Exception e) {
            log.error("socket error", e);
        }
        return null;
    }
}
