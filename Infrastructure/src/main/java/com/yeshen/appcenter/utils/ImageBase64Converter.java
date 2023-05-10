package com.yeshen.appcenter.utils;

import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

/**
 * Date 2022/10/14  9:49
 * author  by HuBingKuan
 */
@Slf4j
public class ImageBase64Converter {
    public static String convertFileToBase64(String imgPath) {
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(imgPath);
            log.info("图片转Base64,文件大小(字节):{}", in.available());
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            log.warn("图片转Base64出错,异常原因:{}", e.getMessage(), e);
        }
        // 对字节数组进行Base64编码,得到Base64编码的字符串
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    /**
     * 将Base64字符串，生成文件
     */
    public static File convertBase64ToFile(String fileBase64String, String filePath, String fileName) {
        File dir = new File(filePath);
        if (!dir.exists() && dir.isDirectory()) {
            dir.mkdirs();
        }
        File file = new File(filePath + File.separator + fileName);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bfile = decoder.decodeBuffer(fileBase64String);
            bos.write(bfile);
            return file;
        } catch (Exception e) {
            log.warn("Base64字符串转为图片失败,异常原因:{}", e.getMessage());
            return null;
        }
    }
}