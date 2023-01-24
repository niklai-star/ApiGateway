package com.niklai.apigateway.processor;

import com.niklai.apigateway.predicate.ApiReadBodyRoutePredicateFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class ApiGatewayBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        beanDefinitionRegistry.removeBeanDefinition("readBodyPredicateFactory");
        BeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(ApiReadBodyRoutePredicateFactory.class)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .setRole(BeanDefinition.ROLE_SUPPORT)
                .getBeanDefinition();
        beanDefinitionRegistry.registerBeanDefinition("readBodyPredicateFactory", beanDefinition);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
