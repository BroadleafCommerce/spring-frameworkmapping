/**
 * 
 */
package org.broadleafcommerce.frameworkmapping;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.broadleafcommerce.frameworkmapping.NestedSingleControllerTest.FrameworkEndpoint;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkGetMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkRestController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Validates that you can have a nested {@literal @FrameworkController} inside of a test class as
 * long as it is a manual bean
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@WebMvcTest(controllers = FrameworkEndpoint.class)
@ExtendWith(SpringExtension.class)
public class NestedSingleControllerTest {

    @Configuration
    static class Config {
        @Bean
        public FrameworkEndpoint endpoint() {
            return new FrameworkEndpoint();
        }
    }

    @FrameworkRestController
    static class FrameworkEndpoint {

        @FrameworkGetMapping("/nested-get")
        public String getMapping() {
            return "Success!";
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void foundActivatedController() throws Exception {
        mockMvc.perform(get("/nested-get"))
                .andExpect(status().isOk())
                .andExpect(content().string("Success!"));
    }

}
