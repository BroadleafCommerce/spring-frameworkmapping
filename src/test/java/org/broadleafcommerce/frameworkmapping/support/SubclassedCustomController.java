package org.broadleafcommerce.frameworkmapping.support;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// TODO re-enable this once subclassing of framework controllers works again
//@RestController
public class SubclassedCustomController extends DefaultFrameworkController {

    @RequestMapping(path = "/subclass-extended-get", method = RequestMethod.GET)
    public String extendedFrameworkOnlyGet() {
        return super.frameworkOnlyGet() + " - Extended";
    }
}
