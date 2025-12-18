package org.zhj.proxy;

import cn.hutool.core.lang.UUID;
import com.github.rholder.retry.*;
import org.zhj.annotation.Retry;
import org.zhj.config.RpcServiceConfig;
import org.zhj.dto.RpcReq;
import org.zhj.dto.RpcResp;
import org.zhj.enums.RpcRespStatus;
import org.zhj.exception.RpcException;
import org.zhj.transmission.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
                .reqId(UUID.randomUUID().toString())
                .interfaceName(method.getDeclaringClass().getCanonicalName())
                .methodName(method.getName())
                .params(args)
                .paramTypes(method.getParameterTypes())
                .version(rpcServiceConfig.getVersion())
                .group(rpcServiceConfig.getGroup())
                .build();
        Retry retry = method.getAnnotation(Retry.class);
        if (Objects.isNull(retry)) {
            return sendReq(rpcReq);
        }
        Retryer<Object> retryer = RetryerBuilder.newBuilder()
                .retryIfExceptionOfType(retry.value())
                .withStopStrategy(StopStrategies.stopAfterAttempt(retry.maxRetries()))
                .withWaitStrategy(WaitStrategies.fixedWait(retry.retryDelay(), TimeUnit.MILLISECONDS))
                .build();
        return retryer.call(() -> sendReq(rpcReq));
    }

    private Object sendReq(RpcReq rpcReq) throws InterruptedException, ExecutionException {
        Future<RpcResp<?>> future = rpcClient.sendReq(rpcReq);
        RpcResp<?> rpcResp = future.get();
        check(rpcReq, rpcResp);
        return rpcResp.getData();
    }

    public void check(RpcReq req, RpcResp<?> resp) {
        if (Objects.isNull(resp)) {
            throw new RpcException("响应为空");
        }
        if (!Objects.equals(req.getReqId(), resp.getReqId())) {
            throw new RpcException("请求和响应的id不一致");
        }
    }
}
