package org.broadleafcommerce;

import org.broadleafcommerce.frameworkmapping.annotation.EnableAllFrameworkControllers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FrameworkApplication {

    @EnableAllFrameworkControllers
    public static class EnableBroadleafControllers {}

    public static void main(String[] args) {
        SpringApplication.run(FrameworkApplication.class, args);
    }
}