package org.broadleafcommerce.frameworkmapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubclassedCustomController extends DefaultFrameworkController {

    @RequestMapping(path = "/subclass-extended-get", method = RequestMethod.GET)
    public String extendedFrameworkOnlyGet() {
        return super.frameworkOnlyGet() + " - Extended";
    }
}
