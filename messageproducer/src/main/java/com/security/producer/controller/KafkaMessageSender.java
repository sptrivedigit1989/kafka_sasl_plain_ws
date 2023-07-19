package com.security.producer.controller;

import com.security.producer.msgs.KafkaMessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Configuration
@RequestMapping("/send")
public class KafkaMessageSender {

    @Value("${spring.kafka.topic-name}")
    String topicName;

    private final KafkaMessageProducer kafkaMessageProducer;

    @Autowired
    public KafkaMessageSender(KafkaMessageProducer kafkaMessageProducer) {
        this.kafkaMessageProducer = kafkaMessageProducer;
    }

    @PostMapping("/{msg}")
    public String postMsg(@PathVariable String msg){
        System.out.println("started pushing data into topic "+topicName);
        kafkaMessageProducer.sendMessage(topicName, msg);
        System.out.println("pushed successfully");
        return "success";
    }

}
