package org.zhj.proxy;

import org.zhj.config.RpcServiceConfig;
import org.zhj.dto.RpcReq;
import org.zhj.dto.RpcResp;
import org.zhj.enums.RpcRespStatus;
import org.zhj.transmission.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

public class RpcClientProxy implements InvocationHandler {
    private final RpcClient rpcClient;
    private final RpcServiceConfig rpcServiceConfig;

    public RpcClientProxy(RpcClient rpcClient) {
        this(rpcClient, new RpcServiceConfig());
    }

    public RpcClientProxy(RpcClient rpcClient, RpcServiceConfig rpcServiceConfig) {
        this.rpcClient = rpcClient;
        this.rpcServiceConfig = rpcServiceConfig;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcReq rpcReq = RpcReq.builder()
                .interfaceName(method.getDeclaringClass().getCanonicalName())
                .methodName(method.getName())
                .params(args)
                .paramTypes(method.getParameterTypes())
                .version(rpcServiceConfig.getVersion())
                .group(rpcServiceConfig.getGroup())
                .build();
        RpcResp<?> rpcResp = rpcClient.sendReq(rpcReq);
        check(rpcReq, rpcResp);
        return rpcResp.getData();
    }

    public void check(RpcReq req, RpcResp<?> resp) {
        if (Objects.isNull(resp)) {
            throw new RuntimeException("响应为空");
        }
        if (!Objects.equals(req.getReqId(), resp.getReqId())) {
            throw new RuntimeException("请求和响应的id不一致");
        }
        if (RpcRespStatus.isFail(resp.getCode())) {
            throw new RuntimeException("响应失败");
        }
    }
}
