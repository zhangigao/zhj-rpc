package org.zhj.transmission.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.zhj.dto.RpcReq;
import org.zhj.dto.RpcResp;

@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String str) throws Exception {
        log.debug("服务端收到请求：{}", str);
        channelHandlerContext.writeAndFlush(str);
        channelHandlerContext.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端发生异常", cause);
        ctx.close();
    }
}
