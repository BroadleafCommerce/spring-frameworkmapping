/**
 *
 */
package org.broadleafcommerce.frameworkmapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Controller
public class DummyController {

    @GetMapping("/endpoint")
    public void doNothing() {
        // returns a 200
    }
}
