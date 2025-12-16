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
import org.zhj.dto.RpcReq;
import org.zhj.dto.RpcResp;
import org.zhj.transmission.RpcClient;
import org.zhj.transmission.netty.codec.NettyRpcDecoder;
import org.zhj.transmission.netty.codec.NettyRpcEncoder;

@Slf4j
public class NettyRpcClient implements RpcClient {
    private static final Bootstrap bootstrap;
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;

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
    public RpcResp<?> sendReq(RpcReq req){
        ChannelFuture future = null;
        try {
            future = bootstrap.connect("127.0.0.1", 8888).sync();
        } catch (InterruptedException e) {
            log.error("连接服务器异常", e);
        }
        Channel channel = future.channel();
        channel.writeAndFlush(req.toString()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        // 等待channel关闭
        try {
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("客户端发生异常", e);
        }
        // 获取响应
        AttributeKey<String> key = AttributeKey.valueOf(RpcConstant.RESPONSE_KET);
        String s = channel.attr(key).get();
        log.info("服务端返回结果：{}", s);
        return null;
    }
}
