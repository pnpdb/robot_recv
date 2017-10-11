package com.suspnp.robot.protocol;


import com.suspnp.robot.utils.ConstantValue;
import lombok.Data;

/**
 * Created by lianhai on 2017-09-11.
 * <p>
 * <pre>
 * 自己定义传输协议格式
 * +--------------+-----------+------------+------------+----------+-------+
 * |  协议开始标志  |  消息类型  |  包总长度   |  文件名长度  |  文件名  |  数据  |
 * +--------------+-----------+------------+-------------+---------+-------+
 * 1.协议开始标志pack_head，为int类型的数据，用16进制0X76表示
 * 2.消息类型: pack_type，int类型
 * 3.传输数据的总长度contentLength，int类型
 * 4.文件名长度fileLength，int类型
 * 5.文件名
 * 6.图像数据
 * </pre>
 */
@Data
public class ImageTransferProtocol {

    /* 包的开始标志 */
    private int packHead = ConstantValue.PACK_HEAD;

    /* 消息类型(心跳或图片) */
    private int packType;

    /* 包的总长度 */
    private int contentLength;

    /* 文件名长度 */
    private int fileNameLength;

    /* 文件名 */
    private byte[] fileName;

    /* 包的内容 */
    private byte[] content;

    public ImageTransferProtocol(int packType) {
        this.packType = packType;
    }

    public ImageTransferProtocol(int contentLength, byte[] fileName, byte[] content) {
        this.packType = ConstantValue.CUSTOM_MSG;
        this.contentLength = contentLength;
        this.fileName = fileName;
        this.content = content;
        this.fileNameLength = fileName.length;
    }

    @Override
    public String toString() {
        if (packType == 3) {
            return "ImageTransferProtocol [pack_head=" + packHead + ", packType=" + packType + ", contentLength="
                    + contentLength + ", fileLength=" + fileName.length + ", fileName=" + new String(fileName) + " " +
                    "contentSize=" + content.length + "]";
        }
        return "ImageTransferProtocol [pack_head=" + packHead + ", packType="
                + packType + "]";
    }
}
