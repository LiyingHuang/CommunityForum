package com.coral.community.dao;

import com.coral.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MessageMapper {

    // 1
    // query the conversation list of the current user
    // return the newest message of each conversation
    List<Message> selectConversations(int userId, int offset, int limit);

    // 2
    // query the count of the conversation
    int  selectConversationCount(int userId);

    // 3
    // query the message list of a specific conversation
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 4
    // query message count of a specific conversation
    int selectLetterCount(String conversationId);

    // 5
    // query the count of the unread message(dynamic:conversationId optional)
    // with conversationId: count the unread of a specific conversation
    // without conversationId: count the total unread of a user/all the conversationId
    int selectLetterUnreadCount(int userId, String conversationId);

    // 6
    // insert message to db
    int insertMessage(Message message);

    // 7
    // change message status (unread -> read)
    int updateStatus(List<Integer> ids, int status);
}
