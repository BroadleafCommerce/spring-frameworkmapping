package org.broadleafcommerce.frameworkmapping.support;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Samarth Dhruva (samarthd)
 */
@RestController
public class CustomController {

    /*
     * Overrides the default endpoint
     */
    @GetMapping("/overridden-get")
    public String overridingGetEndpoint() {
        return "customControllerGetResponse";
    }

    @GetMapping("/custom-only-get")
    public String customOnlyGet() {
        return "customControllerOnlyGetResponse";
    }
}
