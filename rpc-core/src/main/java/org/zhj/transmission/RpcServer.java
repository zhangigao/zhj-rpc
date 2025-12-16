package org.zhj.transmission;

import org.zhj.config.RpcServiceConfig;

public interface RpcServer {

    void start();

    void publishService(RpcServiceConfig config);
}
