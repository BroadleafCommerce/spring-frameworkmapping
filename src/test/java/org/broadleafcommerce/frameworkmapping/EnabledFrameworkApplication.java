package org.broadleafcommerce.frameworkmapping;

import org.broadleafcommerce.frameworkmapping.annotation.FrameworkControllerScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EnabledFrameworkApplication {

    @FrameworkControllerScan(basePackages = "org.broadleafcommerce.frameworkmapping.support")
    public static class EnableBroadleafControllers {}

    public static void main(String[] args) {
        SpringApplication.run(EnabledFrameworkApplication.class, args);
    }
}