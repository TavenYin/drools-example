package org.example.drools.web.hello;

import com.google.common.collect.Lists;
import org.example.drools.web.hello.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HelloWorld
 *
 * @author tianwen.yin
 */
@Slf4j
@RestController
@RequestMapping("hello")
public class HelloController {

    @Autowired
    private KieContainer kieContainer;

    @GetMapping
    public void helloWorld() {
        KieSession kieSession = kieContainer.newKieSession("HelloWorldKS");
        Message message1 = newFact();
        // 将 fact 插入到 ksession 工作内存中
        kieSession.insert( message1 );
        // 触发规则
        kieSession.fireAllRules();
        // 使用结束后，释放 ksession
        kieSession.dispose();


        log.info("*********************************************");
        log.info("use stateless session");
        // 无状态 Session 内部使用的也是有状态 Session
        // 无状态 Session 不需要手动释放资源，但是要一次性传入所有对象或者命令
        StatelessKieSession statelessKS = kieContainer.newStatelessKieSession("StatelessKS");
        Message message2 = newFact();
        statelessKS.execute(Lists.newArrayList(message2));

        log.info("*********************************************");
        log.info("use sequential stateless session");
        // 使用顺序模式后，引擎会按照顺序进行一次评估，如果 insert 或者 modify 触发了一个低于当前优先级的规则，则不会执行
        // 如果将 helloworld.drl 中两个规则顺序调换，依旧会触发 "Good Bye"
        // 规则优先级取决位置（越靠上优先级越高）和 salience（值越大优先级越高）
        StatelessKieSession sequentialKS = kieContainer.newStatelessKieSession("SequentialKS");
        Message message3 = newFact();
        sequentialKS.execute(Lists.newArrayList(message3));
    }

    private Message newFact() {
        Message message = new Message();
        message.setMessage( "Hello World" );
        message.setStatus( Message.HELLO );
        return message;
    }

}
