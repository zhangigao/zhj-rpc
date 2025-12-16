package org.zhj.transmission;

import org.zhj.dto.RpcReq;
import org.zhj.dto.RpcResp;

/**
 * @Author 86155
 * @Date 2025/8/25
 */
public interface RpcClient {

    RpcResp<?> sendReq(RpcReq req);
}
