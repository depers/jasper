package cn.bravedawn.jasper;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

/**
 * @author : depers
 * @program : jasper
 * @date : Created in 2024/2/21 10:14
 */
public class FragmentTest {


    @Test
    public void test() {
        String s = Matcher.quoteReplacement("${123}");
        System.out.println(s);
    }
}
