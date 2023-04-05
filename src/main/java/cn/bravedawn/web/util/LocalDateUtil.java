package cn.bravedawn.web.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/4/5 14:49
 */
public class LocalDateUtil {


    public static String toLocalDateTimeStr(Date date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("Asia/Shanghai"));
        return dtf.format(ldt);
    }
}
