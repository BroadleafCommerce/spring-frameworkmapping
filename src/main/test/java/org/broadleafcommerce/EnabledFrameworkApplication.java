package org.broadleafcommerce;

import org.broadleafcommerce.frameworkmapping.annotation.EnableAllFrameworkControllers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EnabledFrameworkApplication {

    @EnableAllFrameworkControllers
    public static class EnableBroadleafControllers {}

    public static void main(String[] args) {
        SpringApplication.run(EnabledFrameworkApplication.class, args);
    }
}