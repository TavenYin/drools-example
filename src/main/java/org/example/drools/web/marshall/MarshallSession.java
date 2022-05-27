package org.example.drools.web.marshall;

import org.example.drools.web.hello.model.Message;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * KieSession 编组（marshall）到一个文件中，再从文件中解组（unmarshall）
 *
 * @author tianwen.yin
 */
@RestController
public class MarshallSession {

    @Autowired
    private KieContainer kContainer;

    String fileName = System.getProperty("user.dir") + "\\marshall.txt";

    @GetMapping("marshall")
    public void marshall() throws IOException {
        KieSession kieSession = kContainer.newKieSession("HelloWorldKS");
        Message message = new Message();
        message.setMessage( "Hello World" );
        message.setStatus( Message.HELLO );
        // 将 fact 插入到 ksession 工作内存中
        kieSession.insert( message );

        OutputStream out = new FileOutputStream(fileName);
        Marshaller marshaller = KieServices.Factory.get().getMarshallers().newMarshaller( kieSession.getKieBase() );
        marshaller.marshall( out, kieSession );
        kieSession.dispose();
    }

    @GetMapping("unmarshall")
    public void unmarshall() throws IOException, ClassNotFoundException {
        KieBase kbase = kContainer.getKieBase("HelloWorldKB");
        Marshaller marshaller = KieServices.Factory.get().getMarshallers().newMarshaller(kbase);
        KieSession ksession = marshaller.unmarshall(new FileInputStream(fileName), kContainer.getKieSessionConfiguration("HelloWorldKS"), null);
        ksession.fireAllRules();
    }

    @GetMapping("unmarshall2")
    public void unmarshall2() throws IOException, ClassNotFoundException {
        KieHelper kieHelper = new KieHelper();
        kieHelper.addResource(ResourceFactory.newClassPathResource("org/example/drools/hello/helloworld.drl"), ResourceType.DRL);
        KieBase kbase = kieHelper.build();
        Marshaller marshaller = KieServices.Factory.get().getMarshallers().newMarshaller(kbase);
        KieSession ksession = marshaller.unmarshall(new FileInputStream(fileName), kContainer.getKieSessionConfiguration("HelloWorldKS"), null);
        ksession.fireAllRules();
    }

}
