package org.broadleafcommerce.frameworkmapping.support;

import org.broadleafcommerce.frameworkmapping.annotation.FrameworkMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkRestController;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Samarth Dhruva (samarthd)
 */
@FrameworkRestController
@Scope(proxyMode = ScopedProxyMode.NO)
public class DefaultFrameworkControllerNoProxy {

    /*
      This endpoint should not be overridden.
    */
    @FrameworkMapping(path = "/framework-only-get-noproxy", method = RequestMethod.GET)
    public String frameworkOnlyGetNoProxy() {
        return "frameworkControllerOnlyGetResponseNoProxy";
    }
}
