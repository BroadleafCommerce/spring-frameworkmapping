package org.broadleafcommerce.frameworkmapping;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.broadleafcommerce.frameworkmapping.annotation.FrameworkController;
import org.broadleafcommerce.frameworkmapping.support.ControllerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(includeFilters = @ComponentScan.Filter(classes = FrameworkController.class))
@ExtendWith(SpringExtension.class)
public class DisabledFrameworkMappingTest {

    @Configuration
    @Import(ControllerConfig.class)
    static class Config { }

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testFrameworkOnlyGetMappingDisabled() throws Exception {
        // way to register the super method, shouldn't be found
        mockMvc.perform(get("/framework-only-get"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testExtendedEndpointWorks() throws Exception {
        mockMvc.perform(get("/subclass-extended-get"))
                .andExpect(status().isOk())
                .andExpect(content().string("frameworkControllerOnlyGetResponse - Extended"));
    }

    @Test
    public void testOverrideGetMappingStillWorks() throws Exception {
        mockMvc.perform(get("/overridden-get"))
                .andExpect(status().isOk())
                .andExpect(content().string("customControllerGetResponse"));
    }

    @Test
    public void testCustomOnlyGetMappingWorks() throws Exception {
        mockMvc.perform(get("/custom-only-get"))
                .andExpect(status().isOk())
                .andExpect(content().string("customControllerOnlyGetResponse"));
    }

}
