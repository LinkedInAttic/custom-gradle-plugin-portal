package com.linkedin.portal.resources.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "com.linkedin.portal.resources")
@EnableJpaRepositories("com.linkedin.portal.resources.dao")
public class ResourceConfig {
}
