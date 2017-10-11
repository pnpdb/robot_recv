package com.suspnp.robot.utils;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by lianhai on 2017-09-11.
 */
public class ImageUtils {

    /**
     * 图片转字节数组
     *
     * @param imgSrc
     * @return
     */
    public static byte[] image2Bytes(String imgSrc) {
        try {
            FileInputStream fin = new FileInputStream(new File(imgSrc));
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            fin.close();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字节数组转图片
     *
     * @param data
     * @param imgSrc
     */
    public static void byte2Image(byte[] data, String imgSrc) {
        if (data.length < 3 || imgSrc.equals("")) return;
        try {
            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(imgSrc));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
