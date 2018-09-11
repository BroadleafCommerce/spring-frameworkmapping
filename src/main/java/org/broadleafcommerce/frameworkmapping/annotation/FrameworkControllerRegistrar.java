/**
 * 
 */
package org.broadleafcommerce.frameworkmapping.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

/**
 * WIP
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class FrameworkControllerRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
            BeanDefinitionRegistry registry) {
        if (importingClassMetadata != null
                && importingClassMetadata.isAnnotated(FrameworkController.class.getName())) {

            // TODO: this needs to somehow build a BeanDefinition based on the FrameworkController
            // component. Not exactly sure yet what to model this after

            BeanDefinition bean = new RootBeanDefinition(importingClassMetadata.getClassName());
            List<MultiValueMap<String, Object>> valuesHierarchy =
                    captureMetaAnnotationValues(importingClassMetadata);
            Map<String, Object> annotationAttributes =
                    importingClassMetadata
                            .getAnnotationAttributes(MessagingGateway.class.getName());
            replaceEmptyOverrides(valuesHierarchy, annotationAttributes);
            annotationAttributes.put("serviceInterface", importingClassMetadata.getClassName());

            BeanDefinitionReaderUtils.registerBeanDefinition(this.parse(annotationAttributes),
                    registry);
        }
    }
}
