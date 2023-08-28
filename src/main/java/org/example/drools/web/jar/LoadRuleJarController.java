package org.example.drools.web.jar;

import lombok.extern.slf4j.Slf4j;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@Slf4j
@RestController
@RequestMapping("jar")
public class LoadRuleJarController {

    private final KieContainer kieContainer;

    public LoadRuleJarController() {
        ReleaseId releaseId = new ReleaseIdImpl("org.example", "rule-rep", "1.0-SNAPSHOT");
        String jarName = "rule-rep-1.0-SNAPSHOT.jar";

        File ruleJar = new File(System.getProperty("user.dir") + "\\drl\\" + jarName);

        InternalKieModule kieModule = InternalKieModule.createKieModule(releaseId, ruleJar);
        KieServices.get().getRepository().addKieModule(kieModule);
        this.kieContainer = KieServices.get().newKieContainer(releaseId);
        KieSession kSession = kieContainer.newKieSession("RuleRepKSession");
        kSession.fireAllRules();
        kSession.dispose();
    }

    @GetMapping("run")
    public void run() {
        KieSession kSession = kieContainer.newKieSession("RuleRepKSession");
        kSession.fireAllRules();
        kSession.dispose();
    }

}