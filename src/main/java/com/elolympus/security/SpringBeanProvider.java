package com.elolympus.security;

import com.vaadin.collaborationengine.CollaborationEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBeanProvider {
    @Bean
    public CollaborationEngineConfiguration ceConfigBean() {
        CollaborationEngineConfiguration configuration = new CollaborationEngineConfiguration(
                licenseEvent -> {
                    // See <<ce.production.license-events>>
                });
        //cambiar por la ubicacion de la carpeta de datos en tu pc
        configuration.setDataDir("C:/Users/IRVING/.vaadin/collaboration-engine");
        return configuration;
    }
}
