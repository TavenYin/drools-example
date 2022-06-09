# 食用指南

本教程目前已包括如下功能的演示

## 1. 正向推理

org.example.drools.web.forward

## 2. 反向推理

org.example.drools.web.backward

## 3. 决策表，决策表模板

org.example.drools.web.decisiontable

## 4. 数据驱动模板

org.example.drools.web.datadriventemplate

## 5. 流式处理

org.example.drools.web.stream

## 6. kSession 编组解组

org.example.drools.web.marshall

## 7. kContainer 更新

org.example.drools.web.container

## 8. Drools API 生成 drl 

org.example.drools.test.descr

# 常见问题

1. 编译决策表等方式生成 DRL 该如何使用？

    可以用于构建 KieFileSystem
   
    ```java
    String drl = ...;
    KieFileSystem kfs = ks.newKieFileSystem();
    kfs.write( "src/main/resources/org/example/drools/hello/helloworld.drl",
            ResourceFactory.newByteArrayResource(drl.getBytes(StandardCharsets.UTF_8)) );
    ```
