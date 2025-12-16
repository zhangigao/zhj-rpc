package org.zhj.transmission.socket.server;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.zhj.dto.RpcReq;
import org.zhj.dto.RpcResp;
import org.zhj.handler.RpcReqHandler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
@AllArgsConstructor
public class SocketReqHandler implements Runnable {
    private final Socket socket;
    private final RpcReqHandler rpcReqHandler;


    @SneakyThrows
    @Override
    public void run() {
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        RpcReq req = (RpcReq) objectInputStream.readObject();
        log.info("receive req: {}", req);
        Object result = rpcReqHandler.invoke(req);
        RpcResp<?> resp = RpcResp.success(req.getReqId(), result);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(resp);
        objectOutputStream.flush();
    }
}
