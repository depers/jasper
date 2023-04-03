package cn.bravedawn.ioc.utils;

import java.lang.reflect.Field;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/4/3 21:35
 */
public class ReflectionUtils {


    public static void injectField(Field field, Object obj, Object value) throws IllegalAccessException {
        if(field != null) {
            field.setAccessible(true);
            field.set(obj, value);
        }
    }
}
