package org.example.drools.web.stream;

import org.example.drools.web.stream.model.Heartbeat;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
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
    private KieContainer kieContainer;

    @GetMapping("heartbeat")
    public void heartbeat() throws InterruptedException {
        KieSession kieSession = kieContainer.newKieSession("StreamKS");
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
        KieSession kieSession = kieContainer.newKieSession("StreamKS");
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

}

