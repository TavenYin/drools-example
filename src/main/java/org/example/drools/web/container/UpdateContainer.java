package org.example.drools.web.container;

import org.example.drools.web.hello.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 该示例演示了如何更新 KieContainerImpl
 *
 * @author tianwen.yin
 */
@Slf4j
@RestController
@RequestMapping("container")
public class UpdateContainer {

    KieServices ks = KieServices.Factory.get();

    private KieContainerImpl kContainer;

    public UpdateContainer() {
        KieFileSystem kfs = ks.newKieFileSystem();
        // kfs
        kfs.write("src/main/resources/org/example/drools/hello/helloworld.drl",
                ResourceFactory.newClassPathResource("org/example/drools/hello/helloworld.drl"));
        kfs.write("src/main/resources/META-INF/kmodule.xml",
                ResourceFactory.newClassPathResource("META-INF/kmodule.xml"));

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        kieBuilder.buildAll();
        // releaseId 与 pom 中声明的一致
        // 如果 kfs 中未写入 pom 的话，这里也不需要指定 releaseId
        this.kContainer = (KieContainerImpl) ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
    }

    @GetMapping("update")
    public void update() {
        KieFileSystem kfs = ks.newKieFileSystem();
        // 注意修改 fileResource 的路径
        kfs.write("src/main/resources/org/example/drools/hello/helloworld.drl",
                ResourceFactory.newFileResource(System.getProperty("user.dir") + "\\drl\\helloworld.drl"));
        kfs.write("src/main/resources/META-INF/kmodule.xml",
                ResourceFactory.newClassPathResource("META-INF/kmodule.xml"));
        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        KieModule kieModule = kieBuilder.getKieModule();
        kContainer.updateToKieModule((InternalKieModule) kieModule);
    }

    @GetMapping("run")
    public void run() {
        KieSession kieSession = kContainer.newKieSession("HelloWorldKS");
        Message message = new Message();
        message.setMessage( "Hello World" );
        message.setStatus( Message.HELLO );
        // 将 fact 插入到 ksession 工作内存中
        kieSession.insert( message );
        // 触发规则
        kieSession.fireAllRules();
        // 使用结束后，释放 ksession
        kieSession.dispose();
    }

    @GetMapping("runAfterUpdate")
    public void runAfterUpdate() {
        KieSession kieSession = kContainer.newKieSession("HelloWorldKS");
        Message message = new Message();
        message.setMessage( "Hello World" );
        message.setStatus( Message.HELLO );

        this.update();

        // 将 fact 插入到 ksession 工作内存中
        kieSession.insert( message );
        // 触发规则
        kieSession.fireAllRules();
        // 使用结束后，释放 ksession
        kieSession.dispose();
    }

}
