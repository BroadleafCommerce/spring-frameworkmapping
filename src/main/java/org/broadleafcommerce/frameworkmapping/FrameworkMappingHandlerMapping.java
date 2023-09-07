/*
 * #%L BroadleafCommerce Common Libraries %% Copyright (C) 2009 - 2017 Broadleaf Commerce %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0 (the "Fair Use License"
 * located at http://license.broadleafcommerce.org/fair_use_license-1.0.txt) unless the restrictions
 * on use therein are violated and require payment to Broadleaf in which case the Broadleaf End User
 * License Agreement (EULA), Version 1.1 (the "Commercial License" located at
 * http://license.broadleafcommerce.org/commercial_license-1.1.txt) shall apply.
 *
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the
 * "Custom License") between you and Broadleaf Commerce. You may not use this file except in
 * compliance with the applicable license. #L%
 */
package org.broadleafcommerce.frameworkmapping;

import org.broadleafcommerce.frameworkmapping.annotation.FrameworkController;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkControllerScan;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkRestController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * HandlerMapping to find and map {@link FrameworkMapping FrameworkMappings} inside
 * {@link FrameworkController} and {@link FrameworkRestController} classes.
 * <p>
 * When framework controllers are enabled with {@link FrameworkControllerScan}, and a class is
 * annotated with {@link FrameworkController} or {@link FrameworkRestController}, then this class
 * will add {@link FrameworkMapping FrameworkMappings} found within the class to handler mappings.
 * This class has a lower priority than the default
 * {@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping} so
 * when a request comes in, {@link org.springframework.web.bind.annotation.RequestMapping
 * RequestMappings} located inside a class annotated with {@link Controller} or
 * {@link RestController} will have a higher priority and be found before {@link FrameworkMapping
 * FrameworkMappings} found within a {@link FrameworkController} or {@link FrameworkRestController}.
 * <p>
 * The site handler mappings in play in order of precedence from highest to lowest are:
 * <ol>
 * <li>{@link RequestMappingHandlerMapping}</li>
 * <li>{@link FrameworkMappingHandlerMapping}</li>
 * </ol>
 *
 * @author Philip Baggett (pbaggett)
 * @see FrameworkControllerScan
 * @see FrameworkController
 * @see FrameworkRestController
 * @see FrameworkMapping
 */
public class FrameworkMappingHandlerMapping extends RequestMappingHandlerMapping {

    public static final int REQUEST_MAPPING_ORDER = Ordered.LOWEST_PRECEDENCE - 2;

    public FrameworkMappingHandlerMapping() {
        setOrder(REQUEST_MAPPING_ORDER);
    }

    /**
     * See AopUtils and ClassUtils. We want to generally prevent traversal up of super classes for
     * determninig if beans fit within this handler mapping. However, if the controller itself is
     * proxied (which can happen with @PreAuthorize @Transaction or other annotations on controller
     * methods) then the "real" class is actually "super class" of the type passed in to this
     * method. This util ensures that we always get the real bean type from the CGLib proxy type
     * passed in here
     * 
     * @see AOPUtils
     * @see ClassUtils
     * @see AnnotationUtils
     * @see AnnotatedElementUtils
     */
    @Override
    protected boolean isHandler(Class<?> beanType) {
        Class<?> actualBeanType =
                ClassUtils.isCglibProxyClass(beanType) ? ClassUtils.getUserClass(beanType)
                        : beanType;

        // This explicitly searches for the annotation on the current element and any
        // meta-annotations.
        // This intentionally _does not_ look at any super classes to detect the annotation. The
        // explicit cast is here to prevent using the findAnnotation method that takes in a Class<?>
        // and searches up into all super classes and interfaces
        return AnnotationUtils.findAnnotation((AnnotatedElement) actualBeanType,
                FrameworkController.class) != null
                || AnnotationUtils.findAnnotation((AnnotatedElement) actualBeanType,
                        FrameworkMapping.class) != null;

    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        configureMatchOptionalTrailingSeparator();
        RequestMappingInfo requestMappingInfo = createFrameworkRequestMappingInfo(method);
        if (requestMappingInfo != null) {
            RequestMappingInfo typeInfo = createFrameworkRequestMappingInfo(handlerType);
            if (typeInfo != null) {
                requestMappingInfo = typeInfo.combine(requestMappingInfo);
            }
        }

        return requestMappingInfo;
    }

    /**
     * Ideally, this would be configured in {@link #afterPropertiesSet()}. However, the
     * implementation in the super class does not allow for us to customize the instantiated pattern
     * parser before the super-super {@link AbstractHandlerMethodMapping#afterPropertiesSet()}
     * method is called (which creates all the handler mappings).
     * <p>
     * Thus, we invoke this method at the later stage in
     * {@link #getMappingForMethod(Method, Class)}.
     */
    private void configureMatchOptionalTrailingSeparator() {
        // New approach is to redirect instead of matching trailing slash.
        // However, this can have performance implications. Keeping deprecated approach for now.
        getBuilderConfiguration().getPatternParser().setMatchOptionalTrailingSeparator(true);
    }

    private RequestMappingInfo createFrameworkRequestMappingInfo(AnnotatedElement element) {
        FrameworkMapping frameworkMapping =
                AnnotatedElementUtils.findMergedAnnotation(element, FrameworkMapping.class);

        // necessary to avoid NullPointerException in AnnotationUtils.synthesizeAnnotation()
        if (frameworkMapping == null) {
            return null;
        }
        frameworkMapping = AnnotationUtils.synthesizeAnnotation(frameworkMapping, null);
        return (frameworkMapping != null
                ? createRequestMappingInfo(
                        convertFrameworkMappingToRequestMapping(frameworkMapping), null)
                : null);
    }

    private RequestMapping convertFrameworkMappingToRequestMapping(
            final FrameworkMapping frameworkMapping) {
        return new RequestMapping() {
            @Override
            public String name() {
                return frameworkMapping.name();
            }

            @Override
            public String[] value() {
                return frameworkMapping.value();
            }

            @Override
            public String[] path() {
                return frameworkMapping.path();
            }

            @Override
            public RequestMethod[] method() {
                return frameworkMapping.method();
            }

            @Override
            public String[] params() {
                return frameworkMapping.params();
            }

            @Override
            public String[] headers() {
                return frameworkMapping.headers();
            }

            @Override
            public String[] consumes() {
                return frameworkMapping.consumes();
            }

            @Override
            public String[] produces() {
                return frameworkMapping.produces();
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return RequestMapping.class;
            }
        };
    }
}
