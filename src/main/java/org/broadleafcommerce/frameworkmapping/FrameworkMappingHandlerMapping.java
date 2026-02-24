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
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkMapping;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;

public class FrameworkMappingHandlerMapping extends RequestMappingHandlerMapping {

    public static final int REQUEST_MAPPING_ORDER = Ordered.LOWEST_PRECEDENCE - 2;

    public FrameworkMappingHandlerMapping() {
        setOrder(REQUEST_MAPPING_ORDER);
    }

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
        // 1. We removed configureMatchOptionalTrailingSeparator() as it's no longer possible
        RequestMappingInfo requestMappingInfo = createFrameworkRequestMappingInfo(method);
        if (requestMappingInfo != null) {
            RequestMappingInfo typeInfo = createFrameworkRequestMappingInfo(handlerType);
            if (typeInfo != null) {
                requestMappingInfo = typeInfo.combine(requestMappingInfo);
            }

            // 2. NEW: Explicitly add trailing slash variations to the mapping info
            requestMappingInfo = applyTrailingSlashMatching(requestMappingInfo);
        }

        return requestMappingInfo;
    }

    /**
     * Replaces the old 'configureMatchOptionalTrailingSeparator' by explicitly adding both '/path'
     * and '/path/' to the mapping metadata.
     */
    private RequestMappingInfo applyTrailingSlashMatching(RequestMappingInfo info) {
        Set<String> paths = info.getDirectPaths();
        if (paths.isEmpty()) {
            return info;
        }

        // Generate the "other" version of every path (add or remove slash)
        Set<String> additionalPaths = paths.stream()
                .map(path -> path.endsWith("/") ? path.substring(0, path.length() - 1) : path + "/")
                .collect(Collectors.toSet());
        paths.addAll(additionalPaths);
        // Mutate the existing mapping to include both variations
        return info.mutate()
                .paths(paths.toArray(new String[0]))
                .build();
    }

    private RequestMappingInfo createFrameworkRequestMappingInfo(AnnotatedElement element) {
        FrameworkMapping frameworkMapping =
                AnnotatedElementUtils.findMergedAnnotation(element, FrameworkMapping.class);

        // necessary to avoid NullPointerException in AnnotationUtils.synthesizeAnnotation()
        if (frameworkMapping == null) {
            return null;
        }
        frameworkMapping = AnnotationUtils.synthesizeAnnotation(frameworkMapping, null);
        // In Spring 7, we use the RequestMappingInfo builder directly with current options
        return RequestMappingInfo
                .paths(frameworkMapping.path().length > 0 ? frameworkMapping.path()
                        : frameworkMapping.value())
                .methods(frameworkMapping.method())
                .params(frameworkMapping.params())
                .headers(frameworkMapping.headers())
                .consumes(frameworkMapping.consumes())
                .produces(frameworkMapping.produces())
                .mappingName(frameworkMapping.name())
                .options(getBuilderConfiguration()) // Use the mapping's current config
                .build();
    }

    // Note: convertFrameworkMappingToRequestMapping is no longer strictly needed
    // if you use the Builder pattern shown above.
}
