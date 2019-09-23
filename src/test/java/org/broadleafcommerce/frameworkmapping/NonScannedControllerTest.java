/**
 * 
 */
package org.broadleafcommerce.frameworkmapping;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@WebMvcTest
@ExtendWith(SpringExtension.class)
public class NonScannedControllerTest {

    @Configuration
    static class Config {

        @Bean
        public UnscannedController frameworkController() {
            return new UnscannedController();
        }

        @FrameworkRestController
        static class UnscannedController {

            @FrameworkGetMapping("/standalone")
            public String standalone() {
                return "standalone";
            }

            @FrameworkGetMapping("/override")
            public String override() {
                return "super";
            }
        }

        @RestController
        static class NormalController {
            @GetMapping("/override")
            public String override() {
                return "sub";
            }
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void nonOverriddenEndpointWorks() throws Exception {
        mockMvc.perform(get("/standalone"))
                .andExpect(status().isOk())
                .andExpect(content().string("standalone"));
    }

    @Test
    public void normalControllerOverrides() throws Exception {
        mockMvc.perform(get("/override"))
                .andExpect(status().isOk())
                .andExpect(content().string("sub"));
    }

}
