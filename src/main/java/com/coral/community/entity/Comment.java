package com.coral.community.entity;

import java.util.Date;

/*
 *-----------------------------------show comment----------------------------------
 *  1. Data Layer(DAO) -> Comment Mapper
 *     * get one page comment data according Entity
 *     * get/find quantity of comment according to entity
 * 2. Service Layer
 *    * handle get comment service
 *    * handle get quantity of the comment service
 * 3. Controller Layer
 *    * show the detail of the post, mean while, show all the comment data of the post
 *
 * Note: a lot of entity can be comment (entity_type like post/comment/comment user target id/course...)
 *
 * entity_id: specific post/course/comment
 * target_id: specific user
 * */

public class Comment {
    private int id;
    private int userId;
    private int entityType;
    private int targetId;
    private String content;
    private int status;
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", targetId=" + targetId +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
