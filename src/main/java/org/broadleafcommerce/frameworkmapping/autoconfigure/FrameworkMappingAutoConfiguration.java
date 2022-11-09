package org.broadleafcommerce.frameworkmapping.autoconfigure;

import org.broadleafcommerce.frameworkmapping.FrameworkMappingHandlerMapping;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * Main Boot auto configuration for framework mappings. Referenced in META-INF/spring.factories
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
public class FrameworkMappingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FrameworkMappingHandlerMapping frameworkControllerHandlerMapping() {
        return new FrameworkMappingHandlerMapping();
    }

    /**
     * Don't fail a missing path match in the implementation's request mapping if it's available in
     * the framework mapping
     */
    @Bean
    @ConditionalOnMissingBean
    public WebMvcRegistrations frameworkRequestMappingLenientOverride(FrameworkMappingHandlerMapping frameworkMapping) {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return new RequestMappingHandlerMapping() {

                    @Override
                    protected HandlerMethod handleNoMatch(Set<RequestMappingInfo> infos,
                            String lookupPath,
                            HttpServletRequest request) throws ServletException {
                        boolean hasFrameworkMatch;
                        try {
                            hasFrameworkMatch = frameworkMapping.getHandler(request) != null;
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                        if (hasFrameworkMatch) {
                            return null;
                        }
                        return super.handleNoMatch(infos, lookupPath, request);
                    }

                };
            }
        };
    }

}
