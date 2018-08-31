package org.broadleafcommerce.frameworkmapping.support;

import org.broadleafcommerce.frameworkmapping.annotation.FrameworkGetMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkRestController;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Samarth Dhruva (samarthd)
 */
@FrameworkRestController
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
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

    @FrameworkGetMapping(path = "/framework-convenience-get")
    public String convenienceGetMapping() {
        return "frameworkConvenienceGet";
    }

}
