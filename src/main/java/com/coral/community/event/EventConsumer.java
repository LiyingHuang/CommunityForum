package com.coral.community.event;

import com.alibaba.fastjson.JSONObject;
import com.coral.community.entity.Event;
import com.coral.community.entity.Message;
import com.coral.community.service.MessageService;
import com.coral.community.util.CommunityConstant;
import com.coral.community.util.CommunityUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_FOLLOW,TOPIC_LIKE})
    public void handleCommentMethod(ConsumerRecord record){

        if(record == null || record.value() == null){
            logger.error("Record Content is Empty!");
            return;
        }
// record -> event
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);

        if(event == null){
            logger.error("Message Format Incorrect! ");
        }

        // store the notification in message table:
        // conversationId -> Topic/TYPE comment/follow/like
        // content -> jsonObject

        // event -> message
        // json
        // Send notification
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic()); // Topic:string
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType()); // EntityType:1 2 3
        content.put("entityId", event.getEntityId());

        if(!event.getData().isEmpty()) {
            for(Map.Entry<String,Object> entry : event.getData().entrySet()){
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);

    }
}
