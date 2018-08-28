package org.broadleafcommerce;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.broadleafcommerce.frameworkmapping.FrameworkControllerHandlerMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(includeFilters = {@ComponentScan.Filter(classes = FrameworkController.class),
                              @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                                                    classes = FrameworkControllerHandlerMapping.class)})
@RunWith(SpringRunner.class)
public class FrameworkMappingTest {

    @Autowired
    MockMvc mockMvc;


    @Test
    public void testFrameworkOnlyGetMapping() throws Exception {
        mockMvc.perform(get("/framework-only-get"))
                .andExpect(status().isOk())
                .andExpect(content().string("frameworkControllerOnlyGetResponse"));
    }

    @Test
    public void testOverrideGetMapping() throws Exception {
        mockMvc.perform(get("/overridden-get"))
                .andExpect(status().isOk())
                .andExpect(content().string("customControllerGetResponse"));
    }

    @Test
    public void testCustomOnlyGetMapping() throws Exception {
        mockMvc.perform(get("/custom-only-get"))
                .andExpect(status().isOk())
                .andExpect(content().string("customControllerOnlyGetResponse"));
    }

}