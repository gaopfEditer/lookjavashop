package com.mdd.common.util;

import com.mdd.common.config.GlobalConfig;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/**
 * 常用工具集合
 */
public class ToolUtils {

    /**
     * 制作UUID
     *
     * @author fzr
     * @return String
     */
    public static String makeUUID(){
        return UUID.randomUUID().toString().replaceAll("-","").substring(0,32);
    }

    /**
     * 制作MD5
     *
     * @author fzr
     * @param data 需加密的数据
     * @return String
     */
    public static String makeMd5(String data){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte [] array = md5.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 生成唯一Token
     *
     * @author fzr
     * @return String
     */
    public static String makeToken() {
        long millisecond =  System.currentTimeMillis();
        String randStr =  ToolUtils.randomString(8);
        String secret  = GlobalConfig.secret;
        String token   = ToolUtils.makeMd5(ToolUtils.makeUUID() + millisecond + randStr);
        return ToolUtils.makeMd5(token + secret) + ToolUtils.randomString(6);
    }

    /**
     * 返回随机字符串
     *
     * @author fzr
     * @param length 要生成的长度
     * @return String
     */
    public static String randomString(int length) {
        Random random = new Random();
        StringBuilder stringBuffer = new StringBuilder();
        String str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int strLength = str.length();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(strLength);
            stringBuffer.append(str.charAt(index));
        }
        return stringBuffer.toString();
    }

    /**
     * 返回随机数字字符串
     *
     * @author fzr
     * @param length 要生成的长度
     * @return String
     */
    public static String randomInt(int length) {
        Random random = new Random();
        StringBuilder stringBuffer = new StringBuilder();
        String str = "0123456789";
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(10);
            stringBuffer.append(str.charAt(index));
        }
        return stringBuffer.toString();
    }

    /**
     * 转换存储单位: KB MB GB TB
     *
     * @author fzr
     * @return String
     */
    public static String storageUnit(Long size) {
        if (size == null) {
            return "0B";
        }
        if (size < 1024) {
            return size + "B";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            return size + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            size = size * 100;
            return (size / 100) + "." + (size % 100) + "MB";
        } else {
            size = size * 100 / 1024;
            return (size / 100) + "." + (size % 100) + "GB";
        }
    }

    /**
     * 下载文件
     *
     * @author fzr
     * @param urlStr   (文件网址)
     * @param savePath (保存路径,如: /www/uploads/aa.png)
     * @throws IOException IO异常
     */
    public static void download(String urlStr, String savePath) throws IOException {
        ByteArrayOutputStream bos = null;
        FileOutputStream fos = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5*1000);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            inputStream = conn.getInputStream();

            // 获取数组数据
            byte[] buffer = new byte[4*1024];
            int len;
            bos = new ByteArrayOutputStream();
            while((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byte[] getData = bos.toByteArray();

            // 新创建文件夹
            String fileName = StringUtils.substringAfterLast(savePath, "/");
            String path = savePath.replace("/"+fileName, "");
            File saveDir = new File(path);
            if(!saveDir.exists()) {
                if (!saveDir.mkdirs()) {
                    throw new IOException("创建存储文件夹失败");
                }
            }
            // 保存文件数据
            File file = new File(savePath);
            fos = new FileOutputStream(file);
            fos.write(getData);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException ignored) {}
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignored) {}
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {}
            }
        }
    }

    /**
     * 字符串*号替换
     *
     * @author cjhao
     * @param str
     * @return String
     */
    public static String caseStarSymbol(String str) {
        String newStr = null;
        char[] strArr = str.toCharArray();
        if (strArr.length == 1) {
            newStr = str;
        }
        if (strArr.length == 2) {
            newStr = str.replaceFirst(str.substring(1), "*");
        }
        if (strArr.length > 2) {
            newStr = str.replaceFirst(str.substring(1, strArr.length - 1), "*");
        }
        return newStr;
    }


    /**
     * 将数字字符串转换为星期几的格式，如果有7天则显示"每日"
     *
     * @param numbers 逗号分隔的数字字符串，例如 "1,2,3,4,5,6,7"
     * @return 转换后的星期字符串或"每日"（如果所有天数都被包括）
     */
    public static String convertNumbersToWeekdays(String numbers) {
        String[] WEEKDAYS = {
                "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期天"
        };
        // 去除字符串两端的空格，并按逗号分割
        String[] numberArray = numbers.trim().split(",");

        // 用来存储转换后的星期名称
        StringBuilder result = new StringBuilder();

        // 标记是否所有天数都被包括
        boolean allDaysIncluded = true;

        // 遍历每个数字，转换为对应的星期名称
        for (String number : numberArray) {
            int dayIndex = Integer.parseInt(number.trim()) - 1; // 数组索引从0开始，所以减1

            // 检查索引是否在有效范围内
            if (dayIndex >= 0 && dayIndex < WEEKDAYS.length) {
                if (result.length() > 0) {
                    result.append(",");
                }
                result.append(WEEKDAYS[dayIndex]);
            } else {
                // 如果索引无效，则不包括所有天数
                allDaysIncluded = false;
                break;
            }
        }
        // 如果所有天数都被包括，则返回"每日"
        return allDaysIncluded && result.toString().split(",").length == WEEKDAYS.length ? "每日" : result.toString();
    }
}
