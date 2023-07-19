# kafka_sasl_plain_ws

************** SASL/PLAIN Kafka Security Demo **************

step-1 
setup PLAINTEXT(no security) kafka cluster : https://kafka.apache.org/downloads
C:/kafka
update zookeeper.properties and server.properties

start local kafka server
C:\kafka>bin\windows\zookeeper-server-start.bat config\zookeeper.properties
C:\kafka>bin\windows\kafka-server-start.bat config\server.properties


step -2
create normal kafka consumer spring boot project 

add 
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
</dependency>


step-3
#-------------------no auth config ----------------
#spring.kafka.bootstrap-servers=localhost:9092
server.port=8081
spring.kafka.consumer.enable-auto-commit=false
spring.kafka.consumer.auto-offset-reset=earliest


step -4 

@Component
public class NoAuthConsumer {

    @KafkaListener(topics = "qkstrt01", groupId = "no_auth_cg_id_01")
    public void receiveMessage(ConsumerRecord<String, String> record) {
        System.out.println("Received message: " + record.value());
        System.out.println("**** Completed ****");
    }

}


step-5
create spring boot kafka producer app


step-5.1 add 
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
</dependency>

step-5.2
#-------------------no auth config ----------------
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.topic-name=qkstrt01

step-5.3
@Component
public class KafkaMessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaMessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

}

step-5.4
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

NOTE : check consumer app server port e.g. server.port=8081

step-6
start both prod cons spring boot app 

step-7
prod app : send msg to kafka topic 
postmap : localhost:8080/send/hi9

step-8 :  msg should be consumed by consumer app 

********* enable kafka security i.e. SASL/PLAIN authentication *********

step-9 : Create kafka_server_jaas.conf and add it inside C:\kafka\config 
KafkaServer {
    org.apache.kafka.common.security.plain.PlainLoginModule required
    username="admin"
    password="admin-secret"
	user_admin="admin-secret"
    user_alice="alice-secret";
};

step-10 : set system environment variable

KAFKA_OPTS
-Djava.security.auth.login.config=C:\kafka\config\kafka_server_jaas.conf

step-11 : update server.properties
listeners=SASL_PLAINTEXT://localhost:9092
advertised.listeners=SASL_PLAINTEXT://localhost:9092
security.inter.broker.protocol=SASL_PLAINTEXT
sasl.enabled.mechanisms=PLAIN
sasl.mechanism.inter.broker.protocol=PLAIN

step-12 : start kafka cluster [note : SASL/PLAIN kafka security enabled now]


step-13 : start prod and cons app again this time communication won't happen bcz our app don't 
provide the authentication data. 

step-14 : check kafka server logs 
kafka cluster will throw
Unexpected Kafka request of type METADATA during SASL handshake.
Failed authentication with /127.0.0.1 



step-15 : Kafka Client [prod/con] App must provide the authentication data 


producer app SASL/PLAIN configuration
#-------------------auth config ----------------
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.topic-name=qkstrt02
spring.kafka.producer.properties.security.protocol=SASL_PLAINTEXT
spring.kafka.producer.properties.sasl.mechanism=PLAIN
spring.kafka.producer.properties.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required \
    username="admin" \
    password="admin-secret";


consumer app SASL/PLAIN configuration
#-------------------auth config ----------------
server.port=8081
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.enable-auto-commit=false
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.properties.security.protocol=SASL_PLAINTEXT
spring.kafka.consumer.properties.sasl.mechanism=PLAIN
spring.kafka.consumer.properties.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required \
    username="admin" \
    password="admin-secret";

 
step-16 : start prod and cons app again this time communication will happen successfully.

 

thank you.


imp cmd 
produce some msgs
C:\kafka>bin\windows\kafka-topics.bat --create --topic qkstrt01 --bootstrap-server localhost:9092
C:\kafka>bin\windows\kafka-console-producer.bat --topic qkstrt01 --bootstrap-server localhost:9092

