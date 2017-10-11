package com.suspnp.robot.coder;

import com.suspnp.robot.protocol.ImageTransferProtocol;
import com.suspnp.robot.utils.ConstantValue;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by lianhai on 2017-09-11.
 */
public class ImagePackEncoder extends MessageToByteEncoder<ImageTransferProtocol> {

    protected void encode(ChannelHandlerContext channelHandlerContext, ImageTransferProtocol protocol,
                          ByteBuf byteBuf) throws Exception {
        // 写入包的开始的标志
        byteBuf.writeInt(protocol.getPackHead());
        // 写入包类型
        byteBuf.writeInt(protocol.getPackType());
        if (protocol.getPackType() == ConstantValue.CUSTOM_MSG) {
            // 写入包的总长度
            byteBuf.writeInt(protocol.getContentLength());
            // 写入文件名的长度
            byteBuf.writeInt(protocol.getFileNameLength());
            // 写入文件名
            byteBuf.writeBytes(protocol.getFileName());
            // 写入图像的内容
            byteBuf.writeBytes(protocol.getContent());
        }
    }
}
