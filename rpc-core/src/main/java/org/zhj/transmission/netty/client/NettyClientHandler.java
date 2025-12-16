package org.zhj.transmission.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.zhj.constant.RpcConstant;
import org.zhj.dto.RpcMsg;
import org.zhj.dto.RpcResp;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcMsg> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcMsg rpcMsg) throws Exception {
        log.debug("服务端返回结果：{}", rpcMsg);

        RpcResp<?> resp = (RpcResp<?>) rpcMsg.getData();

        AttributeKey<Object> key = AttributeKey.valueOf(RpcConstant.RESPONSE_KET);
        channelHandlerContext.channel().attr(key).set(resp);
        channelHandlerContext.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端发生异常", cause);
        ctx.close();
    }
}
