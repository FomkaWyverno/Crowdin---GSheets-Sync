package ua.wyverno;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:key.context.properties", encoding = "UTF-8")
public class AppConfigurator { }
