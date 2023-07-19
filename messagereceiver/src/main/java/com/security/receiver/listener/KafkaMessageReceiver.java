package com.security.receiver.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageReceiver {

    @KafkaListener(topics = "qkstrt03", groupId = "no_auth_cg_id_03")
    public void receiveMessage(ConsumerRecord<String, String> record) {
        System.out.println("Received message: " + record.value());
        System.out.println("**** Completed ****");
    }
}
