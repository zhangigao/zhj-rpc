package org.zhj.transmission.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.SneakyThrows;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class NettyRpcClient implements RpcClient {
    private static final Bootstrap bootstrap;
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
    private final ServiceDiscovery serviceDiscovery;
    private final ChannelPool channelPool;


    public NettyRpcClient() {
        this(SingletonFactory.getInstance(ZkServiceDiscovery.class));
    }

    public NettyRpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
        this.channelPool = SingletonFactory.getInstance(ChannelPool.class);
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
                                .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                                .addLast(new NettyRpcDecoder())
                                .addLast(new NettyRpcEncoder())
                                .addLast(new NettyClientHandler());
                    }
                });
    }


    @SneakyThrows
    @Override
    public Future<RpcResp<?>> sendReq(RpcReq req) {
        CompletableFuture<RpcResp<?>> future = new CompletableFuture<>();
        UnProcessedReq.put(req.getReqId(), future);
        InetSocketAddress address = serviceDiscovery.lookupService(req);
        Channel channel = channelPool.getChannel(address, () -> channel(address));
        RpcMsg rpcMsg = RpcMsg.builder()
                .versionType(VersionType.VERSION1)
                .serializeType(SerializeType.KRYO)
                .compressType(CompressType.GZIP)
                .msgType(MsgType.RPC_REQ)
                .data(req)
                .build();

        channel.writeAndFlush(rpcMsg)
                .addListener((ChannelFutureListener) listener -> {
                    if (!listener.isSuccess()) {
                        future.completeExceptionally(listener.cause());
                        channel.close();
                    }
                });
        return future;
    }

    private Channel channel(InetSocketAddress address) {
        try {
            return bootstrap.connect(address).sync().channel();
        } catch (InterruptedException e) {
            log.error("创建连接失败：{}", address, e);
            throw new RuntimeException(e);
        }
    }
}
