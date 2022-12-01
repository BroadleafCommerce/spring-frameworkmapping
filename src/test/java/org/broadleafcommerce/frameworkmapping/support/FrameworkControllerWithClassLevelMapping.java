package org.broadleafcommerce.frameworkmapping.support;

import org.broadleafcommerce.frameworkmapping.annotation.FrameworkGetMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkMapping;
import org.broadleafcommerce.frameworkmapping.annotation.FrameworkRestController;

@FrameworkRestController
@FrameworkMapping("/class-prefix")
public class FrameworkControllerWithClassLevelMapping {

    @FrameworkGetMapping("/method-uri")
    public String getMappingWithMethodUri() {
        return "classPrefixMethodUri";
    }

    @FrameworkGetMapping
    public String getMappingWithoutMethodUri() {
        return "classPrefixOnly";
    }
}
