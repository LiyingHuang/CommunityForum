package com.coral.community.entity;

import java.util.HashMap;
import java.util.Map;

/* --------------------Send Notification-------------------
 *  Trigger Event:
 *       1. after comment, send Notification
 *       2. after like, send Notification
 *       3. after follow, send Notification
 *  Handle Event:
 *       1. Encapsulate the event object
 *       2. Develop the Producer of the event/message
 *       3. Develop the Consumer of the event/message
 * */

public class Event {

    // a likes b
    private String topic;
    private int userId;
    private int entityType; // a
    private int entityId;
    private int entityUserId; // b
    private Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key,value);
        return this;
    }
}
