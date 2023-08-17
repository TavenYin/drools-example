package org.example.drools.web.container;

import lombok.extern.slf4j.Slf4j;
import org.drools.compiler.kie.builder.impl.KieFileSystemImpl;
import org.example.drools.web.hello.model.Message;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 食用指南
 * <br/>
 * <br/>
 * init() => 初始化 KieContainer<br/>
 * update() => 更新 KieContainer<br/>
 * run() => 规则执行<br/>
 * updateAndRun() => 先更新后执行<br/>
 * updateAndRun2() => 用于验证新的 KieContainer 并不会影响旧的
 */
@Slf4j
@RestController
@RequestMapping("container2")
public class UpdateContainer2 {

    ContainerManager containerManager = new ContainerManager();

    public static class ContainerManager {
        KieServices ks = KieServices.Factory.get();
        private KieContainer kc;

        public KieContainer getContainer() {
            return kc;
        }

        public KieContainer createOrUpdateContainer(KieFileSystem kfs) {
            return createOrUpdateContainer(kfs, ks.getRepository().getDefaultReleaseId());
        }

        public KieContainer createOrUpdateContainer(KieFileSystem kfs, ReleaseId releaseId) {
            KieBuilder kieBuilder = ks.newKieBuilder(kfs);
            kieBuilder.buildAll();
            this.kc = ks.newKieContainer(releaseId);
            return this.kc;
        }
    }

    @GetMapping("init")
    public void init() {
        KieFileSystem kfs = new KieFileSystemImpl();
        // kfs
        kfs.write("src/main/resources/org/example/drools/hello/helloworld.drl",
                ResourceFactory.newClassPathResource("org/example/drools/hello/helloworld.drl"));
        kfs.write("src/main/resources/META-INF/kmodule.xml",
                ResourceFactory.newClassPathResource("META-INF/kmodule.xml"));

        containerManager.createOrUpdateContainer(kfs);
    }


    @GetMapping("update")
    public void update() {
        KieFileSystem kfs = new KieFileSystemImpl();
        // 项目根目录的规则文件
        kfs.write("src/main/resources/org/example/drools/hello/helloworld.drl",
                ResourceFactory.newFileResource(System.getProperty("user.dir") + "\\drl\\helloworld.drl"));
        kfs.write("src/main/resources/META-INF/kmodule.xml",
                ResourceFactory.newClassPathResource("META-INF/kmodule.xml"));

        containerManager.createOrUpdateContainer(kfs);
    }

    @GetMapping("run")
    public void run() {
        KieContainer kc = containerManager.getContainer();
        KieSession kieSession = kc.newKieSession("HelloWorldKS");
        Message message = new Message();
        message.setMessage( "Hello World" );
        message.setStatus( Message.HELLO );
        kieSession.insert( message );
        kieSession.fireAllRules();
        kieSession.dispose();
    }

    @GetMapping("updateAndRun")
    public void updateAndRun() {
       this.update();
       this.run();
    }

    @GetMapping("updateAndRun2")
    public void updateAndRun2() {
        KieContainer kc = containerManager.getContainer();
        KieSession kieSession = kc.newKieSession("HelloWorldKS");

        this.update();

        Message message = new Message();
        message.setMessage( "Hello World" );
        message.setStatus( Message.HELLO );
        kieSession.insert( message );
        kieSession.fireAllRules();
        kieSession.dispose();

        kieSession = kc.newKieSession("HelloWorldKS");
        message = new Message();
        message.setMessage( "Hello World" );
        message.setStatus( Message.HELLO );
        kieSession.insert( message );
        kieSession.fireAllRules();
        kieSession.dispose();
    }
}
