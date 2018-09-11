/**
 * 
 */
package org.broadleafcommerce.frameworkmapping.support;

import org.broadleafcommerce.frameworkmapping.annotation.FrameworkMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkRestController;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Helps validate that we can read annotations from proxies
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@FrameworkRestController
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ProxiedFrameworkController {

    /**
     * Should not be extended
     */
    @FrameworkMapping(path = "/framework-only-get-proxy", method = RequestMethod.GET)
    public String frameworkOnlyGetNoProxy() {
        return "frameworkControllerOnlyGetResponseProxy";
    }
}
