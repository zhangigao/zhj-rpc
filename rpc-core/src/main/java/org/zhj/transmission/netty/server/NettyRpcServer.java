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
import org.zhj.transmission.RpcServer;
import org.zhj.transmission.netty.codec.NettyRpcDecoder;
import org.zhj.transmission.netty.codec.NettyRpcEncoder;

@Slf4j
public class NettyRpcServer implements RpcServer {

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
                                .addLast(new NettyServerHandler());
                    }
                });
        try {
            ChannelFuture future = serverBootstrap.bind(RpcConstant.SERVER_PORT).sync();
            log.info("服务启动成功");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动服务失败",e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void publishService(RpcServiceConfig config) {

    }
}
