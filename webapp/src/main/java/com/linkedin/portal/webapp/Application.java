package com.linkedin.portal.webapp;

import com.linkedin.portal.resources.config.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.linkedin.portal")
@EnableJpaRepositories("com.linkedin.portal.resources.dao")
@EntityScan("com.linkedin.portal.resources.dao")
//@Import(ResourceConfig.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
