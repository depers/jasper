package cn.bravedawn.ioc.bean;

import java.util.List;

public class BeanDefinition {

    /**
     * bean名称
     */
    private String name;

    /**
     * class名称
     */
    private String className;

    /**
     * 接口名称
     */
    private String interfaceName;

    /**
     * 构造器参数列表
     */
    private List<ConstructorArg> constructorArgs;

    /**
     * 需要注入的对象参数
     */
    private List<PropertyArg> propertyArgs;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public List<ConstructorArg> getConstructorArgs() {
        return constructorArgs;
    }

    public void setConstructorArgs(List<ConstructorArg> constructorArgs) {
        this.constructorArgs = constructorArgs;
    }

    public List<PropertyArg> getPropertyArgs() {
        return propertyArgs;
    }

    public void setPropertyArgs(List<PropertyArg> propertyArgs) {
        this.propertyArgs = propertyArgs;
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "name='" + name + '\'' +
                ", className='" + className + '\'' +
                ", interfaceName='" + interfaceName + '\'' +
                ", constructorArgs=" + constructorArgs +
                ", propertyArgs=" + propertyArgs +
                '}';
    }
}
