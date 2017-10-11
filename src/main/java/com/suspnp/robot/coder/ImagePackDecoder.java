package com.suspnp.robot.coder;

import com.suspnp.robot.protocol.ImageTransferProtocol;
import com.suspnp.robot.utils.ConstantValue;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by lianhai on 2017-09-11.
 */
public class ImagePackDecoder extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws
            Exception {

        //解码后的实体
        ImageTransferProtocol pack;
//        int BASE_LENGTH = 4 + 4 + 4;
        int BASE_LENGTH = 4 + 4;

        if (byteBuf.readableBytes() < BASE_LENGTH)
            return;

        // 限制包的总大小防止socket流攻击
//            if (byteBuf.readableBytes() > 2048) {
//                System.out.println(">2048");
//                byteBuf.skipBytes(byteBuf.readableBytes());
//            }
        // 记录包头开始的index
        int beginReader;
        while (true) {
            // 获取包头开始的index
            beginReader = byteBuf.readerIndex();
            // 标记包头开始的index
            byteBuf.markReaderIndex();
            // 读到了协议的开始标志，结束while循环
            if (byteBuf.readInt() == ConstantValue.PACK_HEAD) {
                break;
            }
            // 未读到包头，顺序读取下一个字节包头信息的开始标记
            byteBuf.resetReaderIndex();
            byteBuf.readByte();
            if (byteBuf.readableBytes() < BASE_LENGTH) {
                return;
            }
        }

        int packType = byteBuf.readInt();
        if (packType == ConstantValue.CUSTOM_MSG) {
            // 包的总的长度
            int contentLength = byteBuf.readInt();
            // 判断是否读取到完整的包消息
            if (byteBuf.readableBytes() < contentLength) {
                // 还原读指针
                byteBuf.readerIndex(beginReader);
                return;
            }
            int fileNameLength = byteBuf.readInt();
            // 读取fileName数据
            byte[] fileName = new byte[fileNameLength];
            byteBuf.readBytes(fileName);

            //读取图片数据
            byte[] content = new byte[contentLength - 4 - 4 - fileNameLength];
            byteBuf.readBytes(content);

            pack = new ImageTransferProtocol(contentLength, fileName, content);
        } else {
            pack = new ImageTransferProtocol(packType);
        }
        list.add(pack);
    }
}
