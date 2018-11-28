/**
 * 
 */
package org.broadleafcommerce.frameworkmapping;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.broadleafcommerce.frameworkmapping.annotation.FrameworkController;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkControllerScan;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkGetMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkRestController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/**
 * <p>
 * Canary test that validates that using the includes attribute of an {@literal @WebMvcTest} does
 * <i>not</i> actually pick up the controller if it is nested inside of a test class, via the
 * {@link org.springframework.boot.test.context.filter.TestTypeExcludeFilter}.
 * 
 * <p>
 * Also validates that
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@WebMvcTest(includeFilters = @Filter(FrameworkController.class))
@ExtendWith(SpringExtension.class)
public class NestedScannedControllerTest {

    @Configuration
    // this scans the entire package that the outer class is in
    @FrameworkControllerScan(basePackageClasses = FrameworkEndpoint.class)
    static class Config {}

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
    public void didNotFindNestedController() throws Exception {
        mockMvc.perform(get("/nested-get"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void foundNonNestedController() throws Exception {
        mockMvc.perform(get("/framework-only-get"))
                .andExpect(status().isOk())
                .andExpect(content().string("frameworkControllerOnlyGetResponse"));
    }

}
