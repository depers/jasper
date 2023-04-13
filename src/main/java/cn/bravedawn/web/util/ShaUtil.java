package cn.bravedawn.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/4/13 11:35
 */
public class ShaUtil {

    private static final Logger log = LoggerFactory.getLogger(ShaUtil.class);


    public static String sign(String value) {
        try {
            // 创建一个MessageDigest实例:
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            // 反复调用update输入数据:
            md.update(value.getBytes("UTF-8"));
            byte[] result = md.digest();
            return new BigInteger(1, result).toString(16);
        } catch (Exception e) {
            log.error("计算哈希值报错", e);
        }
        return null;
    }
}
