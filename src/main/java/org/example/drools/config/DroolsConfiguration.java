package org.example.drools.config;

import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

/**
 * @author tianwen.yin
 */
@Slf4j
@Configuration
public class DroolsConfiguration {
    public DroolsConfiguration() {
    }

    @Bean
    public KieContainer classpathKContainer() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kc = kieServices.newKieClasspathContainer();
        Collection<String> kBaseNames = kc.getKieBaseNames();

        for (String kBaseName : kBaseNames) {
            Results verify;
            try {
                verify = kc.verify(kBaseName);
            } catch (Exception e) {
                throw new RuntimeException("verify exception");
            }

            if (verify.hasMessages(Message.Level.INFO)) {
                log.info(verify.getMessages(Message.Level.INFO).toString());
            }

            if (verify.hasMessages(Message.Level.WARNING)) {
                log.warn(verify.getMessages(Message.Level.WARNING).toString());
            }

            if (verify.hasMessages(Message.Level.ERROR)) {
                log.error(verify.getMessages(Message.Level.ERROR).toString());
                throw new RuntimeException(String.format("Exception occurred during verify kBase %s", kBaseName));
            }
        }

        return kc;
    }
}
