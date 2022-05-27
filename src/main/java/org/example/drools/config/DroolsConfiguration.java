package org.example.drools.config;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tianwen.yin
 */
@Configuration
public class DroolsConfiguration {
    public DroolsConfiguration() {
    }

    @Bean
    public KieContainer classpathKContainer() {
        KieServices kieServices = KieServices.Factory.get();
        return kieServices.newKieClasspathContainer();
    }
}
