package org.zhj.transmission.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.zhj.constant.RpcConstant;
import org.zhj.dto.RpcMsg;
import org.zhj.dto.RpcReq;
import org.zhj.dto.RpcResp;
import org.zhj.enums.CompressType;
import org.zhj.enums.MsgType;
import org.zhj.enums.SerializeType;
import org.zhj.enums.VersionType;
import org.zhj.factory.SingletonFactory;
import org.zhj.registry.ServiceDiscovery;
import org.zhj.registry.impl.ZkServiceDiscovery;
import org.zhj.transmission.RpcClient;
import org.zhj.transmission.netty.codec.NettyRpcDecoder;
import org.zhj.transmission.netty.codec.NettyRpcEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class NettyRpcClient implements RpcClient {
    private static final Bootstrap bootstrap;
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
    private static final AtomicInteger ID_GEN = new AtomicInteger(0);
    private final ServiceDiscovery serviceDiscovery;


    public NettyRpcClient() {
        this(SingletonFactory.getInstance(ZkServiceDiscovery.class));
    }

    public NettyRpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    static {
        bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, DEFAULT_CONNECT_TIMEOUT)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new NettyRpcDecoder())
                                .addLast(new NettyRpcEncoder())
                                .addLast(new NettyClientHandler());
                    }
                });
    }


    @Override
    public RpcResp<?> sendReq(RpcReq req) {
        InetSocketAddress address = serviceDiscovery.lookupService(req);
        ChannelFuture future = null;
        try {
            future = bootstrap.connect(address).sync();
            log.info("netty rpc connect to {}:{}", address.getHostName(), address.getPort());
        } catch (InterruptedException e) {
            log.error("连接服务器异常", e);
        }
        Channel channel = future.channel();
        RpcMsg rpcMsg = RpcMsg.builder()
                .reqId(ID_GEN.getAndIncrement())
                .versionType(VersionType.VERSION1)
                .serializeType(SerializeType.KRYO)
                .compressType(CompressType.GZIP)
                .msgType(MsgType.RPC_REQ)
                .data(req)
                .build();

        channel.writeAndFlush(rpcMsg)
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        // 等待channel关闭
        try {
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("客户端发生异常", e);
        }
        // 获取响应
        AttributeKey<RpcResp<?>> key = AttributeKey.valueOf(RpcConstant.RESPONSE_KET);
        return channel.attr(key).get();
    }
}
