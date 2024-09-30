package org.powerimo.secret.server.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.powerimo.secret.server.exceptions.InvalidConfigPropertyException;
import org.powerimo.secret.server.generators.CodeGenerator;
import org.powerimo.secret.server.generators.StringCodeGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AppConfig {
    private final AppProperties appProperties;
    private final ApplicationContext applicationContext;

    @Bean
    public CodeGenerator codeGenerator() throws InvalidConfigPropertyException {
        Class<?> beanClass;
        try {
            beanClass = Class.forName(appProperties.getGeneratorClass());
        } catch (ClassNotFoundException ex) {
            throw new InvalidConfigPropertyException("Generator class not found: " + appProperties.getGeneratorClass()
                    + ". Please specify it by setting property 'app.generator-class'. Default value: " + StringCodeGenerator.class.getCanonicalName());
        }

        Object generatorBean;
        try {
            generatorBean = applicationContext.getBean(beanClass);
        } catch (Exception ex) {
            throw new InvalidConfigPropertyException("Bean of the generator class is not found. Please annotate it with '@Component' annotation. Generator class: " + beanClass.getCanonicalName());
        }

        if (generatorBean instanceof CodeGenerator) {
            log.info("Code generator class: {}", generatorBean.getClass().getCanonicalName());
            return (CodeGenerator) generatorBean;
        } else {
            String message = "Generator bean (" + generatorBean.getClass().getCanonicalName() + ") is not implementing interface " + CodeGenerator.class.getCanonicalName();
            throw new InvalidConfigPropertyException(message);
        }
    }

}
