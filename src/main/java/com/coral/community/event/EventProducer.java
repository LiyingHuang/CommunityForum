package com.coral.community.event;

import com.alibaba.fastjson.JSONObject;
import com.coral.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    // Handel Event
    public void fireEvent(Event event){
        // send the event to the specific topic
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
