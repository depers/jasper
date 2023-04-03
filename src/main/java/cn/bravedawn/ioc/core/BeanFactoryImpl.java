package cn.bravedawn.ioc.core;

import cn.bravedawn.ioc.bean.BeanDefinition;
import cn.bravedawn.ioc.bean.ConstructorArg;
import cn.bravedawn.ioc.utils.BeanUtils;
import cn.bravedawn.ioc.utils.ClassUtils;
import cn.bravedawn.ioc.utils.ReflectionUtils;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BeanFactoryImpl implements BeanFactory {


    private static final ConcurrentHashMap<String, Object> beanMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, BeanDefinition> beanDefineMap = new ConcurrentHashMap<>();
    private static final Set<String> beanNameSet = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, Object> earlySingletonObjectMap = new HashMap<String, Object>(16);

    @Override
    public Object getBean(String name) throws Exception {
        // 查找对象是否已经实例化过
        Object bean = beanMap.get(name);
        if (bean != null) {
            return bean;
        }

        Object earlyBean = earlySingletonObjectMap.get(name);
        if (earlyBean != null) {
            System.out.println("循环依赖，提前返回尚未加载完成的bean:" + name);
            return earlyBean;
        }

        // 如果没有实例化，那就需要调用createBean来创建对象
        BeanDefinition beanDefinition = beanDefineMap.get(name);
        bean = createBean(beanDefinition);

        if (bean != null) {
            earlySingletonObjectMap.put(name, bean);

            // 对象创建成功以后，注入对象需要的参数
            populateBean(bean);

            // 再把对象存入Map中方便下次使用。
            beanMap.put(name, bean);

            // 从早期单例Map中移除
            earlySingletonObjectMap.remove(name);
        }

        // 结束返回
        return bean;
    }


    /**
     * 创建Bean
     * @param beanDefinition
     * @return
     * @throws Exception
     */
    private Object createBean(BeanDefinition beanDefinition) throws Exception {
        String beanName = beanDefinition.getClassName();
        Class clz = ClassUtils.loadClass(beanName);
        if (clz == null) {
            throw new Exception("can not find bean by beanName");
        }
        List<ConstructorArg> constructorArgs = beanDefinition.getConstructorArgs();
        if (constructorArgs != null && !constructorArgs.isEmpty()) {
            List<Object> objects = new ArrayList<>();
            for (ConstructorArg constructorArg : constructorArgs) {
                if (constructorArg.getValue() != null) {
                    objects.add(constructorArg.getValue());
                } else {
                    objects.add(getBean(constructorArg.getRef()));
                }
            }
            Class[] constructorArgTypes = objects.stream().map(it -> it.getClass()).collect(Collectors.toList()).toArray(new Class[]{});
            Constructor constructor = clz.getConstructor(constructorArgTypes);
            return BeanUtils.instanceByCglib(clz, constructor, objects.toArray());
        } else {
            return BeanUtils.instanceByCglib(clz, null, null);
        }
    }

    /**
     * 填充字段数据
     * @param bean
     * @throws Exception
     */
    private void populateBean(Object bean) throws Exception {
        Field[] fields = bean.getClass().getSuperclass().getDeclaredFields();
        for (Field field : fields) {
            String beanName = field.getName();
            // 将首字母改为小写
            beanName = StringUtils.uncapitalize(beanName);
            if (beanNameSet.contains(field.getName())) {
                Object fieldBean = getBean(beanName);
                if (fieldBean != null) {
                    ReflectionUtils.injectField(field, bean, fieldBean);
                }
            }
        }
    }

    /**
     * 注册bean
     * @param name
     * @param bd
     */
    protected void registerBean(String name, BeanDefinition bd) {
        beanDefineMap.put(name, bd);
        beanNameSet.add(name);
    }
}
