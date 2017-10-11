package com.suspnp.robot.handler;

import com.suspnp.robot.protocol.ImageTransferProtocol;
import com.suspnp.robot.utils.ConstantValue;
import com.suspnp.robot.utils.ImageUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

/**
 * Created by lianhai on 2017-09-11.
 */
public class ServerHandler extends HeartbeatHandler {

    private static Logger logger = Logger.getLogger(ServerHandler.class);

    public ServerHandler() {
        super("Server");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("channelActive");
    }

    @Override
    protected void handleData(ChannelHandlerContext channelHandlerContext, ImageTransferProtocol body) {
        logger.info(name + " get package: " + body.toString());
        if (body.getPackType() == ConstantValue.CUSTOM_MSG) {
            ImageUtils.byte2Image(body.getContent(), ConstantValue.STATIC_SERVER_PATH + new String(body.getFileName()));
        }
    }

    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);
        logger.info("---client " + ctx.channel().remoteAddress().toString() + " reader timeout, close it---");
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        if (!channel.isActive())
            logger.error(name + "exception, close the channel");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.error("channelInActive");
    }
}
