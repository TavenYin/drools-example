package org.example.drools.web.backward;

import lombok.extern.slf4j.Slf4j;
import org.example.drools.web.backward.model.Location;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 反向推理
 * <br/>
 * 下面这张图清晰的展示了正向推理和反向推理的逻辑
 * <br/>
 * <img width="1024" height="550" src="https://docs.drools.org/7.69.0.Final/drools-docs/html_single/Examples/BackwardChaining/RuleEvaluation.png" alt="">
 * @author tianwen.yin
 */
@Slf4j
@RestController
@RequestMapping("backward")
public class BackwardController {

    @Autowired
    private KieContainer kc;

    /**
     * 反向推理与 query 的用法
     */
    @GetMapping("location")
    public void location() {
        KieSession ksession = kc.newKieSession( "BackwardKS");

        ksession.insert( new Location("Office", "House") );
        ksession.insert( new Location("Kitchen", "House") );
        ksession.insert( new Location("Knife", "Kitchen") );
        ksession.insert( new Location("Cheese", "Kitchen") );
        ksession.insert( new Location("Desk", "Office") );
        ksession.insert( new Location("Chair", "Office") );
        ksession.insert( new Location("Computer", "Desk") );
        ksession.insert( new Location("Drawer", "Desk") );

        ksession.insert( "go1" );
        ksession.fireAllRules();
        System.out.println("---");

        ksession.insert( "go2" );
        ksession.fireAllRules();
        System.out.println("---");

        ksession.insert( "go3" );
        // rule "go3" 的 goal（目标没有达成），将规则标记成 evaluated
        // 当新的 fact 被 flush 时都会尝试进行匹配
        // 如果不需要反向推理，使用  ?isContainedIn 方式调用
        ksession.fireAllRules();
        System.out.println("---");

        ksession.insert( new Location("Key", "Drawer") );
        // 本次 fire 达成了 "go3" 的 goal
        ksession.fireAllRules();
        System.out.println("---");

        ksession.insert( "go4" );
        ksession.fireAllRules();
        System.out.println("---");


        ksession.insert( "go5" );
        ksession.fireAllRules();

        // java api 同样可以调用query
        // ksession.getQueryResults
        // ksession.openLiveQuery
    }
}
