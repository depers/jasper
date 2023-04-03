package cn.bravedawn.ioc.core;

public interface BeanFactory {

    Object getBean(String name) throws Exception;
}
