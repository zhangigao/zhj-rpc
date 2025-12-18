package org.zhj.transmission.netty.client;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.zhj.constant.RpcConstant;
import org.zhj.dto.RpcMsg;
import org.zhj.dto.RpcResp;
import org.zhj.enums.CompressType;
import org.zhj.enums.MsgType;
import org.zhj.enums.SerializeType;
import org.zhj.enums.VersionType;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcMsg> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcMsg rpcMsg) throws Exception {
        if (rpcMsg.getMsgType().isHeartBeat()) {
//            log.debug("收到服务端心跳： {}", rpcMsg);
            return;
        }
        RpcResp<?> resp = (RpcResp<?>) rpcMsg.getData();
        log.debug("服务端返回结果：{}", resp);
        UnProcessedReq.complete(resp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端发生异常", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE) {
            RpcMsg rpcMsg = RpcMsg.builder()
                    .versionType(VersionType.VERSION1)
                    .serializeType(SerializeType.KRYO)
                    .compressType(CompressType.GZIP)
                    .msgType(MsgType.HEARTBEAT_REQ)
                    .build();
            ctx.writeAndFlush(rpcMsg)
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }

        super.userEventTriggered(ctx, evt);
    }
}
