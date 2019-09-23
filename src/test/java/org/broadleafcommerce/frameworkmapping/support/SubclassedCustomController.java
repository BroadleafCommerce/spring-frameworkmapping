package org.broadleafcommerce.frameworkmapping.support;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubclassedCustomController extends DefaultFrameworkController {

    @Override
    @RequestMapping(path = "/subclass-extended-get", method = RequestMethod.GET)
    public ResponseEntity<String> frameworkOnlyGet() {
        return ResponseEntity.ok(super.frameworkOnlyGet().getBody() + " - Extended");
    }
}
