package org.example.drools.web.decisiontable;

import lombok.extern.slf4j.Slf4j;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.example.drools.web.decisiontable.model.Driver;
import org.example.drools.web.decisiontable.model.Policy;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

/**
 * @author tianwen.yin
 */
@Slf4j
@RestController
public class DecisionTableController {

    @Autowired
    private KieContainer kc;

    private Driver createDriver() {
        Driver driver = new Driver();
        driver.setName("Mr Joe Blogs");
        driver.setAge(30);
        driver.setPriorClaims(0);
        driver.setLocationRiskProfile("LOW");
        return driver;
    }

    private Policy createPolicy() {
        Policy policy = new Policy();
        policy.setType("COMPREHENSIVE");
        return policy;
    }

    @GetMapping("decisionTable")
    public void decisionTable() {
        KieSession ksession = kc.newKieSession("DecisionTableKS");
        Driver driver = createDriver();
        Policy policy = createPolicy();
        ksession.insert(driver);
        ksession.insert(policy);
        ksession.fireAllRules();
        System.out.println( "BASE PRICE IS: " + policy.getBasePrice() );
        System.out.println( "DISCOUNT IS: " + policy.getDiscountPercent() );
        ksession.dispose();
    }

    @GetMapping("decisionTable/compiler")
    public void decisionTableCompiler() {
        SpreadsheetCompiler spreadsheetCompiler = new SpreadsheetCompiler();
        InputStream is = ClassLoader.getSystemResourceAsStream("org/example/drools/decisiontable/ExamplePolicyPricing.xls");
        String drl = spreadsheetCompiler.compile(is, InputType.XLS);
        System.out.println(drl);
        System.out.println("----------------------------------------------");
    }

    @GetMapping("ruleTemplate")
    public void ruleTemplate() {
        KieSession ksession = kc.newKieSession( "DTableWithTemplateKS" );

        Driver driver = createDriver();
        Policy policy = createPolicy();

        ksession.insert(driver);
        ksession.insert(policy);

        ksession.fireAllRules();

        System.out.println("BASE PRICE IS: " + policy.getBasePrice());
        System.out.println("DISCOUNT IS: " + policy.getDiscountPercent());

        ksession.dispose();
    }

    @GetMapping("ruleTemplate/compiler")
    public void ruleTemplateCompiler() {
        ExternalSpreadsheetCompiler compiler = new ExternalSpreadsheetCompiler();
        InputStream xls = ClassLoader.getSystemResourceAsStream("org/example/drools/decisiontable-template/ExamplePolicyPricingTemplateData.xls");
        InputStream template1 = ClassLoader.getSystemResourceAsStream("org/example/drools/decisiontable-template/BasePricing.drt");
        String basePriceDrl = compiler.compile(xls, template1, 3, 3);
        System.out.println(basePriceDrl);

        System.out.println("----------------------------------------------");

        // 再读一次文件的原因是 compiler 内部关闭了流
        xls = ClassLoader.getSystemResourceAsStream("org/example/drools/decisiontable-template/ExamplePolicyPricingTemplateData.xls");
        InputStream template2 = ClassLoader.getSystemResourceAsStream("org/example/drools/decisiontable-template/PromotionalPricing.drt");
        String promotionalPricingDrl = compiler.compile(xls, template2, 18, 3);
        System.out.println(promotionalPricingDrl);
    }

}
