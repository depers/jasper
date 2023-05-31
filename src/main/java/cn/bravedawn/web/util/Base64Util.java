package cn.bravedawn.web.util;


import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/4/13 13:56
 */
public class Base64Util {

    public static String decode(String value) {
        return new String(Base64.decodeBase64(value), StandardCharsets.UTF_8);
    }
}
