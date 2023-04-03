package cn.bravedawn.ioc.core;

import cn.bravedawn.ioc.bean.BeanDefinition;
import cn.bravedawn.ioc.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.InputStream;
import java.util.List;

public class ApplicationContext extends BeanFactoryImpl {

    private String fileName;

    public ApplicationContext(String fileName) {
        this.fileName = fileName;
    }

    public void init() {
        loadFile();
    }

    private void loadFile() {

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);

        List<BeanDefinition> beanDefinitions = JsonUtils.readValue(is, new TypeReference<List<BeanDefinition>>() {
        });

        if (beanDefinitions != null && !beanDefinitions.isEmpty()) {

            for (BeanDefinition beanDefinition : beanDefinitions) {
                registerBean(beanDefinition.getName(), beanDefinition);
            }
        }

    }
}
