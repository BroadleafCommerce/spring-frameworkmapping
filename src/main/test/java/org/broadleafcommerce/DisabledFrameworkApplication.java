package org.broadleafcommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DisabledFrameworkApplication {

    // does not enable framework controllers

    public static void main(String[] args) {
        SpringApplication.run(DisabledFrameworkApplication.class, args);
    }
}
