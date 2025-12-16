package org.zhj.handler;

import lombok.extern.slf4j.Slf4j;
import org.zhj.dto.RpcReq;
import org.zhj.provider.ServiceProvider;

@Slf4j
public class RpcReqHandler {

    private final ServiceProvider serviceProvider;

    public RpcReqHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public Object invoke(RpcReq req) {
        Object service = serviceProvider.getService(req.getServiceName());
        try {
            return service.getClass().getMethod(req.getMethodName(), req.getParamTypes()).invoke(service, req.getParams());
        } catch (Exception e) {
            log.error("invoke error", e);
        }
        return null;
    }
}
