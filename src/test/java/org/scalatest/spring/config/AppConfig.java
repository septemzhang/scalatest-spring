package org.scalatest.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages="org.scalatest.spring.service")
public class AppConfig { }

