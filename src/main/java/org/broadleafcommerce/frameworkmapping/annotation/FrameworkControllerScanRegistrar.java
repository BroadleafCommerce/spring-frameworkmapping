/**
 * 
 */
package org.broadleafcommerce.frameworkmapping.annotation;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AspectJTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 
 * WIP: represents WIP towards dynamically registering beans from a classpath scan without a
 * reliance on an @ComponentScan. Modeled after
 * org.springframework.integration.config.IntegrationComponentScanRegistrar which is where much of
 * this code comes from.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class FrameworkControllerScanRegistrar implements ImportBeanDefinitionRegistrar,
        ResourceLoaderAware, EnvironmentAware {

    private final Map<TypeFilter, ImportBeanDefinitionRegistrar> componentRegistrars =
            new HashMap<>();

    private ResourceLoader resourceLoader;

    private Environment environment;

    public FrameworkControllerScanRegistrar() {
        this.componentRegistrars.put(new AnnotationTypeFilter(FrameworkController.class, true),
                new FrameworkControllerRegistrar());
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
            BeanDefinitionRegistry registry) {
        Map<String, Object> componentScan =
                importingClassMetadata
                        .getAnnotationAttributes(FrameworkControllerScan.class.getName());

        Collection<String> basePackages = getBasePackages(importingClassMetadata, registry);

        if (basePackages.isEmpty()) {
            basePackages = Collections
                    .singleton(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }

        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false) {

                    @Override
                    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                        return beanDefinition.getMetadata().isIndependent()
                                && !beanDefinition.getMetadata().isAnnotation();
                    }
                };

        if ((boolean) componentScan.get("useDefaultFilters")) {
            for (TypeFilter typeFilter : this.componentRegistrars.keySet()) {
                scanner.addIncludeFilter(typeFilter);
            }
        }

        for (AnnotationAttributes filter : (AnnotationAttributes[]) componentScan
                .get("includeFilters")) {
            for (TypeFilter typeFilter : typeFiltersFor(filter, registry)) {
                scanner.addIncludeFilter(typeFilter);
            }
        }
        for (AnnotationAttributes filter : (AnnotationAttributes[]) componentScan
                .get("excludeFilters")) {
            for (TypeFilter typeFilter : typeFiltersFor(filter, registry)) {
                scanner.addExcludeFilter(typeFilter);
            }
        }


        scanner.setResourceLoader(this.resourceLoader);

        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    for (ImportBeanDefinitionRegistrar registrar : this.componentRegistrars
                            .values()) {
                        registrar.registerBeanDefinitions(
                                ((AnnotatedBeanDefinition) candidateComponent).getMetadata(),
                                registry);
                    }
                }
            }
        }
    }

    protected Collection<String> getBasePackages(AnnotationMetadata importingClassMetadata,
            BeanDefinitionRegistry registry) {
        Map<String, Object> componentScan =
                importingClassMetadata
                        .getAnnotationAttributes(FrameworkControllerScan.class.getName());

        Set<String> basePackages = new HashSet<>();

        for (String pkg : (String[]) componentScan.get("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }

        for (Class<?> clazz : (Class[]) componentScan.get("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        return basePackages;
    }

    private List<TypeFilter> typeFiltersFor(AnnotationAttributes filter,
            BeanDefinitionRegistry registry) {
        List<TypeFilter> typeFilters = new ArrayList<>();
        FilterType filterType = filter.getEnum("type");

        for (Class<?> filterClass : filter.getClassArray("classes")) {
            switch (filterType) {
                case ANNOTATION:
                    Assert.isAssignable(Annotation.class, filterClass,
                            "An error occurred while processing a @FrameworkControllerScan ANNOTATION type filter: ");
                    @SuppressWarnings("unchecked")
                    Class<Annotation> annotationType = (Class<Annotation>) filterClass;
                    typeFilters.add(new AnnotationTypeFilter(annotationType));
                    break;
                case ASSIGNABLE_TYPE:
                    typeFilters.add(new AssignableTypeFilter(filterClass));
                    break;
                case CUSTOM:
                    Assert.isAssignable(TypeFilter.class, filterClass,
                            "An error occurred while processing a @FrameworkControllerScan CUSTOM type filter: ");
                    TypeFilter typeFilter =
                            BeanUtils.instantiateClass(filterClass, TypeFilter.class);
                    invokeAwareMethods(filter, this.environment, this.resourceLoader, registry);
                    typeFilters.add(typeFilter);
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Filter type not supported with Class value: " + filterType);
            }
        }

        for (String expression : filter.getStringArray("pattern")) {
            switch (filterType) {
                case ASPECTJ:
                    typeFilters.add(new AspectJTypeFilter(expression,
                            this.resourceLoader.getClassLoader()));
                    break;
                case REGEX:
                    typeFilters.add(new RegexPatternTypeFilter(Pattern.compile(expression)));
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Filter type not supported with String pattern: " + filterType);
            }
        }

        return typeFilters;
    }

    private static void invokeAwareMethods(Object parserStrategyBean,
            Environment environment,
            ResourceLoader resourceLoader,
            BeanDefinitionRegistry registry) {
        if (parserStrategyBean instanceof Aware) {
            if (parserStrategyBean instanceof BeanClassLoaderAware) {
                ClassLoader classLoader = (registry instanceof ConfigurableBeanFactory
                        ? ((ConfigurableBeanFactory) registry).getBeanClassLoader()
                        : resourceLoader.getClassLoader());
                ((BeanClassLoaderAware) parserStrategyBean).setBeanClassLoader(classLoader);
            }
            if (parserStrategyBean instanceof BeanFactoryAware && registry instanceof BeanFactory) {
                ((BeanFactoryAware) parserStrategyBean).setBeanFactory((BeanFactory) registry);
            }
            if (parserStrategyBean instanceof EnvironmentAware) {
                ((EnvironmentAware) parserStrategyBean).setEnvironment(environment);
            }
            if (parserStrategyBean instanceof ResourceLoaderAware) {
                ((ResourceLoaderAware) parserStrategyBean).setResourceLoader(resourceLoader);
            }
        }
    }

}
