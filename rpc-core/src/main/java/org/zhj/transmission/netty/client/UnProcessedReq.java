package org.zhj.transmission.netty.client;

import org.zhj.dto.RpcReq;
import org.zhj.dto.RpcResp;
import org.zhj.exception.RpcException;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 15566
 * @version 0.0.1
 * @description: TODO
 * @date 2025/12/18 8:32
 */
public class UnProcessedReq {

    private static final Map<String, CompletableFuture<RpcResp<?>>> COMPLETABLE_FUTURE_MAP = new ConcurrentHashMap<>();

    public static void put(String reqId, CompletableFuture<RpcResp<?>> future) {
        COMPLETABLE_FUTURE_MAP.put(reqId, future);
    }

    public static void complete(RpcResp<?> resp) {
        CompletableFuture<RpcResp<?>> future = COMPLETABLE_FUTURE_MAP.remove(resp.getReqId());
        if (future == null) {
            throw new RpcException("客户端获取响应异常");
        }
        future.complete(resp);

    }
}
