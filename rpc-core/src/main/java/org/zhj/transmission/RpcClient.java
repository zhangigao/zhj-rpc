package org.zhj.transmission;

import org.zhj.dto.RpcReq;
import org.zhj.dto.RpcResp;

import java.util.concurrent.Future;

/**
 * @Author 86155
 * @Date 2025/8/25
 */
public interface RpcClient {

    Future<RpcResp<?>> sendReq(RpcReq req);
}
