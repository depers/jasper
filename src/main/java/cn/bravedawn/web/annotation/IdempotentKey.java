package cn.bravedawn.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IdempotentKey {


    /**
     * 幂等性注解
     */

    /**
     * 序号
     */
    public int order() default 1;
}
