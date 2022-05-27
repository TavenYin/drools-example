package org.example.drools.web.stream;

import org.example.drools.web.stream.model.Heartbeat;
import lombok.extern.slf4j.Slf4j;
import org.example.drools.web.stream.model.SensorReading;
import org.example.drools.web.stream.model.TemperatureThreshold;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Drools 流式处理的用法
 *
 * @author tianwen.yin
 */
@Slf4j
@RestController
@RequestMapping("stream")
public class StreamController {

    @Autowired
    private KieContainer kc;

    @GetMapping("heartbeat")
    public void heartbeat() throws InterruptedException {
        KieSession kieSession = kc.newKieSession("StreamKS");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(kieSession::fireUntilHalt);
        Thread.sleep(500);
        log.info("send Heartbeat");
        kieSession.setGlobal("log", log);
        kieSession.getEntryPoint("MonitoringStream").insert(new Heartbeat());

        log.info("sleep 15s");
        Thread.sleep(15000);
        log.info("exit");
        kieSession.dispose();
        executorService.shutdown();
    }

    @GetMapping("heartbeat/normal")
    public void heartbeatNormal() throws InterruptedException {
        KieSession kieSession = kc.newKieSession("StreamKS");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(kieSession::fireUntilHalt);
        Thread.sleep(500);
        kieSession.setGlobal("log", log);

        for (int i=0; i<5; i++) {
            log.info("send Heartbeat");
            kieSession.getEntryPoint("MonitoringStream").insert(new Heartbeat());
            log.info("sleep 6s");
            Thread.sleep(6000);
        }
        log.info("exit");
        kieSession.dispose();
        executorService.shutdown();
    }

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * 滑动时间窗口演示
     *
     * 计算最近 10s 内的平均温度，当超过温度阈值时触发规则
     */
    @GetMapping("timeWindow")
    public void timeWindow() throws InterruptedException {
        KieSession ksession = kc.newKieSession("TimeWindowKS");
        executorService.execute(ksession::fireUntilHalt);
        ksession.setGlobal("log", log);
        Thread.sleep(500);
        log.info("TemperatureThreshold max is 10");
        ksession.insert(new TemperatureThreshold(10d));

        log.info("insert 9");
        ksession.insert(new SensorReading(9d));

        log.info("insert 11");
        ksession.insert(new SensorReading(11d));
        log.info("sleep 4s");
        Thread.sleep(4000);
        log.info("insert 13");
        ksession.insert(new SensorReading(13d));
        log.info("sleep 4s");
        Thread.sleep(4000);
        log.info("insert 14");
        ksession.insert(new SensorReading(14d));
        Thread.sleep(8000);
        log.info("insert 22");
        ksession.insert(new SensorReading(22d));

        QueryResults results = ksession.getQueryResults( "sensor query" );
        log.info("sensor query " + results.toList());

        Thread.sleep(15000);
        // 该示例中，未指定事件超时事件时，Drools 不会自动删除事件
        results = ksession.getQueryResults( "sensor query" );
        log.info("sensor query after 15s" + results.toList());

        ksession.halt();
        ksession.dispose();
    }

    @GetMapping("lengthWindow")
    public void lengthWindow() throws InterruptedException {
        KieSession ksession = kc.newKieSession("LengthWindowKS");
        executorService.execute(ksession::fireUntilHalt);
        ksession.setGlobal("log", log);
        Thread.sleep(500);
        log.info("TemperatureThreshold max is 10");
        ksession.insert(new TemperatureThreshold(10d));

        log.info("insert 9");
        ksession.insert(new SensorReading(9d));

        log.info("insert 11");
        ksession.insert(new SensorReading(11d));

        log.info("sleep 50 ms");
        Thread.sleep(50);

        log.info("insert 13");
        ksession.insert(new SensorReading(13d));

        log.info("sleep 50 ms");
        Thread.sleep(50);

        log.info("insert 14");
        ksession.insert(new SensorReading(14d));

        log.info("sleep 50 ms");
        Thread.sleep(50);

        log.info("insert 22");
        ksession.insert(new SensorReading(22d));

        Thread.sleep(500);

        ksession.halt();
        ksession.dispose();
    }

}

