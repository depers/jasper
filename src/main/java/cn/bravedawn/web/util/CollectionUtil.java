package cn.bravedawn.web.util;

import java.util.List;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/4/19 15:24
 */
public class CollectionUtil {

    public static <T> boolean judgeEquals(List<T> firstList, List<T> secondList){
        if (firstList.size() != secondList.size()) {
            return false;
        }

        return firstList.stream().filter(secondList::contains).count() == firstList.size();
    }
}
