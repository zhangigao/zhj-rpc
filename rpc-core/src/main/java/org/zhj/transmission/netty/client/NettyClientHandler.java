package org.zhj.transmission.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.zhj.constant.RpcConstant;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,String str) throws Exception {
        log.debug("服务端返回结果：{}", str);
        AttributeKey<Object> key = AttributeKey.valueOf(RpcConstant.RESPONSE_KET);
        channelHandlerContext.channel().attr(key).set(str);
        channelHandlerContext.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端发生异常", cause);
        ctx.close();
    }
}
