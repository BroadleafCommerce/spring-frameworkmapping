package org.broadleafcommerce.frameworkmapping;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.broadleafcommerce.frameworkmapping.annotation.FrameworkMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkRestController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = WildcardMappingTest.TestWildcardController.class)
@ExtendWith(SpringExtension.class)
public class WildcardMappingTest {

    @Configuration
    @Import(TestWildcardController.class)
    static class Config {}

    @Autowired
    MockMvc mockMvc;

    @FrameworkRestController
    static class TestWildcardController {

        @FrameworkMapping("/wildcard/**")
        public String wildcard() {
            return "wildcard";
        }

        @FrameworkMapping("/capture/{*path}")
        public String capture() {
            return "capture";
        }

        @FrameworkMapping("/regular")
        public String regular() {
            return "regular";
        }
    }

    @Test
    public void testWildcardMapping() throws Exception {
        mockMvc.perform(get("/wildcard/foo/bar"))
                .andExpect(status().isOk())
                .andExpect(content().string("wildcard"));

        mockMvc.perform(get("/wildcard/foo/bar/"))
                .andExpect(status().isOk())
                .andExpect(content().string("wildcard"));
    }

    @Test
    public void testCaptureMapping() throws Exception {
        mockMvc.perform(get("/capture/foo/bar"))
                .andExpect(status().isOk())
                .andExpect(content().string("capture"));

        mockMvc.perform(get("/capture/foo/bar/"))
                .andExpect(status().isOk())
                .andExpect(content().string("capture"));
    }

    @Test
    public void testRegularMapping() throws Exception {
        mockMvc.perform(get("/regular"))
                .andExpect(status().isOk())
                .andExpect(content().string("regular"));

        mockMvc.perform(get("/regular/"))
                .andExpect(status().isOk())
                .andExpect(content().string("regular"));
    }

}
