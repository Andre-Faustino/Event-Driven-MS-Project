package com.microservices.kafka.producer.config.service.impl;

import com.andre.faustino.kafka.avro.model.TwitterAvroModel;
import com.microservices.kafka.producer.config.service.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.PreDestroy;

@Service
@Slf4j
public class TwitterKafkaProducer implements KafkaProducer<Long, TwitterAvroModel> {

    private KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;

    public TwitterKafkaProducer(KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String topicName, Long key, TwitterAvroModel message) {
        log.info("Sending message='{}' to topic='{}'", message,topicName);
        ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture =
                kafkaTemplate.send(topicName, key, message);
        addCallback(topicName, message, kafkaResultFuture);
    }

    @PreDestroy
    public void close(){
        if (kafkaTemplate != null){
            log.info("Closing kafka producer!");
            kafkaTemplate.destroy();
        }
    }

    private void addCallback(String topicName, TwitterAvroModel message, ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture) {
        kafkaResultFuture.addCallback(
                result -> {
                    RecordMetadata metadata = result.getRecordMetadata();
                    log.debug("Received new metadata. Topic {}; Partition {}; Offset {}; Timestamp {}, at time {}",
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset(),
                            metadata.timestamp(),
                            System.nanoTime()
                            );
                }, ex -> {
                    log.error("Error while sending message {} to topic {}",
                            message.toString(), topicName, ex);
                });
    }
}
