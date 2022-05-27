package org.example.drools.web.forward;

import org.example.drools.web.forward.model.Fibonacci;
import org.example.drools.web.forward.model.Fire;
import org.example.drools.web.forward.model.Politician;
import org.example.drools.web.forward.model.Room;
import org.example.drools.web.forward.model.Sprinkler;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 正向推理演示
 * 最常用的方式，当前工作内存中的 fact 满足 LHS 时，会执行对应 RHS
 *
 * @author tianwen.yin
 */
@RestController
@RequestMapping("forward")
public class ForwardController {
    @Autowired
    private KieContainer kc;

    /**
     * 计算斐波那契数列
     */
    @GetMapping("fibonacci")
    public void fibonacci() {
        KieSession ksession = kc.newKieSession("FibonacciKS");
        ksession.insert( new Fibonacci( 50 ) );
        ksession.fireAllRules();
        ksession.dispose();
    }

    /**
     * 演示 insert modify delete 用法
     */
    @GetMapping("fire")
    public void fire() {
        KieSession ksession = kc.newKieSession("FireKS");

        String[] names = new String[]{"kitchen", "bedroom", "office", "livingroom"};
        Map<String, Room> name2room = new HashMap<>();
        for( String name: names ){
            Room room = new Room( name );
            name2room.put( name, room );
            ksession.insert( room );
            Sprinkler sprinkler = new Sprinkler( room );
            ksession.insert( sprinkler );
        }

        ksession.fireAllRules();

        System.out.println("-------------------------");

        Fire kitchenFire = new Fire( name2room.get( "kitchen" ) );
        Fire officeFire = new Fire( name2room.get( "office" ) );

        FactHandle kitchenFireHandle = ksession.insert( kitchenFire );
        FactHandle officeFireHandle = ksession.insert( officeFire );
        ksession.fireAllRules();
        System.out.println("-------------------------");

        ksession.delete( kitchenFireHandle );
        ksession.delete( officeFireHandle );
        ksession.fireAllRules();
        ksession.dispose();
    }

    /**
     * 演示 insertLogical 用法
     */
    @GetMapping("politician")
    public void politician() {
        KieSession ksession = kc.newKieSession("HonestPoliticianKS");

        final Politician p1 = new Politician( "President of Umpa Lumpa", true );
        final Politician p2 = new Politician( "Prime Minster of Cheeseland", true );
        final Politician p3 = new Politician( "Tsar of Pringapopaloo", true );
        final Politician p4 = new Politician( "Omnipotence Om", true );

        ksession.insert( p1 );
        ksession.insert( p2 );
        ksession.insert( p3 );
        ksession.insert( p4 );

        ksession.fireAllRules();

        ksession.dispose();
    }

}
