package org.broadleafcommerce.frameworkmapping.autoconfigure;

import org.broadleafcommerce.frameworkmapping.FrameworkControllerHandlerMapping;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Main Boot auto configuration for framework mappings. Referenced in META-INF/spring.factories
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Configuration
public class FrameworkMappingAutoConfiguration {

    @Bean
    public FrameworkControllerHandlerMapping frameworkControllerHandlerMapping() {
        return new FrameworkControllerHandlerMapping();
    }
}
