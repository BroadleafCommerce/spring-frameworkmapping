package org.broadleafcommerce.frameworkmapping;

import org.broadleafcommerce.frameworkmapping.annotation.FrameworkMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkRestController;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Samarth Dhruva (samarthd)
 */
@FrameworkRestController
public class DefaultFrameworkController {

    /*
       This endpoint should not be overridden.
    */
    @FrameworkMapping(path = "/framework-only-get", method = RequestMethod.GET)
    public String frameworkOnlyGet() {
        return "frameworkControllerOnlyGetResponse";
    }


    @FrameworkMapping(path = "/overridden-get", method = RequestMethod.GET)
    public String toBeOverriddenGet() {
        return "frameworkControllerGetResponse";
    }
}
