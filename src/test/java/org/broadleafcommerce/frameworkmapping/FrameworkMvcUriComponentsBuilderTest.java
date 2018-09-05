package org.broadleafcommerce.frameworkmapping;

import static org.broadleafcommerce.frameworkmapping.FrameworkMvcUriComponentsBuilder.fromController;
import static org.broadleafcommerce.frameworkmapping.FrameworkMvcUriComponentsBuilder.fromMethodName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkController;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkControllerScan;
import org.broadleafcommerce.frameworkmapping.support.ControllerConfig;
import org.broadleafcommerce.frameworkmapping.support.DefaultFrameworkController;
import org.broadleafcommerce.frameworkmapping.support.FrameworkControllerWithClassLevelMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@WebMvcTest(includeFilters = {@ComponentScan.Filter(classes = FrameworkController.class),
                              @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                                                    classes = FrameworkControllerHandlerMapping.class)})
@ExtendWith(SpringExtension.class)
public class FrameworkMvcUriComponentsBuilderTest {

    @Configuration
    @Import(ControllerConfig.class)
    @FrameworkControllerScan(basePackages = "org.broadleafcommerce.frameworkmapping.support")
    static class Config {}

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    private void setUpRequestAttributes() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    }

    @Test
    public void testFromController() throws Exception {
        mockMvc.perform(get(fromController(FrameworkControllerWithClassLevelMapping.class)
                .build().toUriString())).andExpect(status().isOk())
                .andExpect(content().string("classPrefixOnly"));
    }

    @Test
    public void testFromMethodNameWithClassPrefix() throws Exception {
        mockMvc.perform(get(fromMethodName(FrameworkControllerWithClassLevelMapping.class,
                                           "getMappingWithMethodUri")
                .build().toUriString())).andExpect(status().isOk())
                .andExpect(content().string("classPrefixMethodUri"));
    }


    @Test
    public void testFromMethodName() throws Exception {
        mockMvc.perform(get(fromMethodName(DefaultFrameworkController.class, "frameworkOnlyGet")
                .build().toUriString())).andExpect(status().isOk())
                .andExpect(content().string("frameworkControllerOnlyGetResponse"));

    }
}
