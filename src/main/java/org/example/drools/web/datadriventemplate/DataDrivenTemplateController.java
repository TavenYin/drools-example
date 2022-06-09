package org.example.drools.web.datadriventemplate;

import lombok.extern.slf4j.Slf4j;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;
import org.example.drools.web.datadriventemplate.model.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author tianwen.yin
 */
@Slf4j
@RestController
public class DataDrivenTemplateController {

    private class TestDataProvider
            implements
            DataProvider {

        private Iterator<FeeScheduleRule> iterator;

        TestDataProvider(List<FeeScheduleRule> rows) {
            this.iterator = rows.iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public String[] next() {
            FeeScheduleRule nextRule = iterator.next();
            String[] row = new String[]{ String.valueOf( nextRule.getFeeEventId() ),
                    nextRule.getType().getCode(),
                    nextRule.getEntityBranch(),
                    nextRule.getProductType().getCode(),
                    nextRule.getActivityType().getName(),
                    nextRule.getFeeType().getCode(),
                    nextRule.getOwningParty(),
                    nextRule.getCurrency(),
                    nextRule.getComparator(),
                    String.valueOf( nextRule.getCompareAmount() ),
                    String.valueOf( nextRule.getAmount() ),
                    String.valueOf( nextRule.isLogEvent() )};
            return row;
        }

    }

    /**
     * 使用数据驱动模板 动态生成 DRL
     */
    @GetMapping("/dataDriven/compile")
    public void compile() {
        ArrayList<FeeScheduleRule> rules = new ArrayList<FeeScheduleRule>();
        FeeScheduleType standard = new FeeScheduleType( "STANDARD" );
        FeeScheduleType flat = new FeeScheduleType( "FLAT" );
        ProductType sblc = new ProductType( "SBLC" );
        ProductType rrc = new ProductType( "RRC" );
        ActivityType iss = new ActivityType( "ISS" );
        ActivityType osx = new ActivityType( "OSX" );
        FeeType commission = new FeeType( "Commission" );
        FeeType postage = new FeeType( "Postage" );
        FeeType telex = new FeeType( "Telex" );

        rules.add( createRule( 1,
                flat,
                "",
                sblc,
                iss,
                commission,
                "Party 1",
                "USD",
                "",
                0,
                750,
                true ) );
        rules.add( createRule( 2,
                standard,
                "Entity Branch 1",
                rrc,
                iss,
                commission,
                "",
                "YEN",
                "",
                0,
                1600,
                false ) );
        rules.add( createRule( 3,
                standard,
                "",
                sblc,
                iss,
                postage,
                "",
                "YEN",
                "",
                0,
                40,
                true ) );
        rules.add( createRule( 4,
                flat,
                "",
                sblc,
                osx,
                telex,
                "",
                "YEN",
                "<",
                30000,
                45,
                false ) );
        TestDataProvider tdp = new TestDataProvider( rules );
        final DataProviderCompiler converter = new DataProviderCompiler();
        final String drl = converter.compile( tdp,
                getTemplate() );
        System.out.println( drl );
    }

    private InputStream getTemplate() {
        return ClassLoader.getSystemResourceAsStream("org/example/drools/datadriventemplate/FeeScheduleRules.drt");
    }

    private FeeScheduleRule createRule(long feeEventId,
                                       FeeScheduleType type,
                                       String entityBranch,
                                       ProductType productType,
                                       ActivityType activityType,
                                       FeeType feeType,
                                       String owningParty,
                                       String currency,
                                       String comparator,
                                       long compareAmount,
                                       long amount,
                                       boolean logEvent) {
        FeeScheduleRule rule = new FeeScheduleRule( feeEventId,
                activityType,
                productType,
                type,
                feeType,
                owningParty,
                entityBranch,
                comparator,
                compareAmount,
                amount,
                currency,
                logEvent );
        return rule;
    }
}
