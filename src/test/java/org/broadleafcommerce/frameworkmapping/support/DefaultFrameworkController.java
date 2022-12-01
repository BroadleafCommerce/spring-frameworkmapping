package org.broadleafcommerce.frameworkmapping.support;

import org.broadleafcommerce.frameworkmapping.annotation.FrameworkDeleteMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkGetMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkPatchMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkPostMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkPutMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkRestController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Samarth Dhruva (samarthd)
 */
@FrameworkRestController
public class DefaultFrameworkController {

    @FrameworkMapping(path = "/framework-only-get", method = RequestMethod.GET)
    public ResponseEntity<String> frameworkOnlyGet() {
        return ResponseEntity.ok("frameworkControllerOnlyGetResponse");
    }

    @FrameworkMapping(path = "/overridden-get", method = RequestMethod.GET)
    public String toBeOverriddenGet() {
        return "frameworkControllerGetResponse";
    }

    // Convenience annotations

    @FrameworkGetMapping(path = "/framework-convenience-get")
    public String convenienceGetMapping() {
        return "frameworkConvenienceGet";
    }

    @FrameworkPostMapping(path = "/framework-convenience-post",
            consumes = MediaType.TEXT_PLAIN_VALUE)
    public String conveniencePostMapping(@RequestBody String requestBody) {
        return "frameworkConveniencePost, requestBody: " + requestBody;
    }

    @FrameworkPutMapping(path = "/framework-convenience-put", consumes = MediaType.TEXT_PLAIN_VALUE)
    public String conveniencePutMapping(@RequestBody String requestBody) {
        return "frameworkConveniencePut, requestBody: " + requestBody;
    }

    @FrameworkDeleteMapping(path = "/framework-convenience-delete")
    public String convenienceDeleteMapping() {
        return "frameworkConvenienceDelete";
    }

    @FrameworkPatchMapping(path = "/framework-convenience-patch",
            consumes = MediaType.TEXT_PLAIN_VALUE)
    public String conveniencePatchMapping(@RequestBody String requestBody) {
        return "frameworkConveniencePatch, requestBody: " + requestBody;
    }

}
