package org.broadleafcommerce.frameworkmapping.support;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubclassedCustomController extends DefaultFrameworkController {

    @Override
    @RequestMapping(path = "/subclass-extended-get", method = RequestMethod.GET)
    public String frameworkOnlyGet() {
        return super.frameworkOnlyGet() + " - Extended";
    }
}
