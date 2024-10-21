package cn.bravedawn.web.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.util.UUID;

/**
 * @author : depers
 * @program : jasper
 * @date : Created in 2024/1/28 16:57
 *
 * jasypt加密工具类
 */
public class JasyptUtil {

    public static void main(String[] args) {
        StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
        /*配置文件中配置如下的算法*/
        standardPBEStringEncryptor.setAlgorithm("PBEWithMD5AndDES");
        /*配置文件中配置的password*/
        String password = UUID.randomUUID().toString();
        standardPBEStringEncryptor.setPassword(password);
        // 加密
        String jasyptPasswordEN = standardPBEStringEncryptor.encrypt("121212");
        // 解密
        String jasyptPasswordDE = standardPBEStringEncryptor.decrypt(jasyptPasswordEN);
        System.out.println("加密后密码：" + jasyptPasswordEN);
        System.out.println("解密后密码：" + jasyptPasswordDE);
    }
}
