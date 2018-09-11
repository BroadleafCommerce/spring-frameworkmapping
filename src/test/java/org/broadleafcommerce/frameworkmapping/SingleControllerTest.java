/**
 * 
 */
package org.broadleafcommerce.frameworkmapping;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.broadleafcommerce.frameworkmapping.annotation.FrameworkControllerScan;
import org.broadleafcommerce.frameworkmapping.support.DefaultFrameworkController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests for the 'controllers' attribute of WebMvcTest
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@WebMvcTest(controllers = DefaultFrameworkController.class)
@ExtendWith(SpringExtension.class)
public class SingleControllerTest {

    @Configuration
    @FrameworkControllerScan(basePackages = "org.broadleafcommerce.frameworkmapping.support")
    static class Config {}

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void foundActivatedController() throws Exception {
        mockMvc.perform(get("/framework-only-get"))
                .andExpect(status().isOk())
                .andExpect(content().string("frameworkControllerOnlyGetResponse"));
    }

    @Test
    public void deactivatedControllerNotFound() throws Exception {
        mockMvc.perform(get("/framework-only-get-proxy"))
                .andExpect(status().isNotFound());
    }
}
