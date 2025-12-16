package org.zhj.transmission.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.zhj.config.RpcServiceConfig;
import org.zhj.constant.RpcConstant;
import org.zhj.factory.SingletonFactory;
import org.zhj.provider.ServiceProvider;
import org.zhj.provider.impl.ZkServiceProvider;
import org.zhj.transmission.RpcServer;
import org.zhj.transmission.netty.codec.NettyRpcDecoder;
import org.zhj.transmission.netty.codec.NettyRpcEncoder;
import org.zhj.util.ShutdownHookUtils;

@Slf4j
public class NettyRpcServer implements RpcServer {

    private final ServiceProvider serviceProvider;
    private final int post;

    public NettyRpcServer() {
        this(RpcConstant.SERVER_PORT);
    }

    public NettyRpcServer(int post) {
        this(SingletonFactory.getInstance(ZkServiceProvider.class), post);
    }

    public NettyRpcServer(ServiceProvider serviceProvider) {
        this(serviceProvider, RpcConstant.SERVER_PORT);
    }

    public NettyRpcServer(ServiceProvider serviceProvider, int post) {
        this.serviceProvider = serviceProvider;
        this.post = post;
    }

    @Override
    public void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline()
                                .addLast(new NettyRpcDecoder())
                                .addLast(new NettyRpcEncoder())
                                .addLast(new NettyServerHandler(serviceProvider));
                    }
                });
        ShutdownHookUtils.clearAll();
        try {
            ChannelFuture future = serverBootstrap.bind(post).sync();
            log.info("netty server started on port {}", post);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动服务失败", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        serviceProvider.publishService(config);
    }
}
