package org.zhj.provider;

import org.zhj.config.RpcServiceConfig;

public interface ServiceProvider {

    void publishService(RpcServiceConfig config);

    Object getService(String serviceName);
}
