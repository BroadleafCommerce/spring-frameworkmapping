package org.broadleafcommerce.frameworkmapping;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.broadleafcommerce.frameworkmapping.annotation.FrameworkController;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkControllerScan;
import org.broadleafcommerce.frameworkmapping.support.ControllerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(includeFilters = @ComponentScan.Filter(classes = FrameworkController.class))
@ExtendWith(SpringExtension.class)
public class EnabledFrameworkMappingTest {

    @Configuration
    @Import(ControllerConfig.class)
    @FrameworkControllerScan(basePackages = "org.broadleafcommerce.frameworkmapping.support")
    static class Config { }

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testFrameworkOnlyGetMappingWorks() throws Exception {
        mockMvc.perform(get("/framework-only-get"))
                .andExpect(status().isOk())
                .andExpect(content().string("frameworkControllerOnlyGetResponse"));
    }

    @Test
    public void testFrameworkOnlyGetMappingNoProxyWorks() throws Exception {
        mockMvc.perform(get("/framework-only-get-noproxy"))
                .andExpect(status().isOk())
                .andExpect(content().string("frameworkControllerOnlyGetResponseNoProxy"));
    }

    @Test
    public void testFrameworkOnlyProxiedGetMappingWorks() throws Exception {
        mockMvc.perform(get("/framework-only-get-proxy"))
                .andExpect(status().isOk())
                .andExpect(content().string("frameworkControllerOnlyGetResponseProxy"));
    }

    @Test
    public void testFrameworkConvenienceGetAnnotationWorks() throws Exception {
        mockMvc.perform(get("/framework-convenience-get"))
                .andExpect(status().isOk())
                .andExpect(content().string("frameworkConvenienceGet"));
    }

    @Test
    public void testFrameworkConveniencePostAnnotationWorks() throws Exception {
        mockMvc.perform(post("/framework-convenience-post")
                        .content("requestBody")
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("frameworkConveniencePost, requestBody: requestBody"));
    }

    @Test
    public void testFrameworkConveniencePutAnnotationWorks() throws Exception {
        mockMvc.perform(put("/framework-convenience-put")
                        .content("requestBody")
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("frameworkConveniencePut, requestBody: requestBody"));
    }

    @Test
    public void testFrameworkConvenienceDeleteAnnotationWorks() throws Exception {
        mockMvc.perform(delete("/framework-convenience-delete"))
                .andExpect(status().isOk())
                .andExpect(content().string("frameworkConvenienceDelete"));
    }

    @Test
    public void testFrameworkConveniencePatchAnnotationWorks() throws Exception {
        mockMvc.perform(patch("/framework-convenience-patch")
                        .content("requestBody")
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("frameworkConveniencePatch, requestBody: requestBody"));
    }


    @Test
    public void testOverrideGetMappingWorks() throws Exception {
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

    @Test
    public void testExtendedEndpointWorks() throws Exception {
        mockMvc.perform(get("/subclass-extended-get"))
                .andExpect(status().isOk())
                .andExpect(content().string("frameworkControllerOnlyGetResponse - Extended"));
    }

}
