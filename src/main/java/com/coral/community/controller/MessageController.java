package com.coral.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.coral.community.dao.MessageMapper;
import com.coral.community.entity.Message;
import com.coral.community.entity.Page;
import com.coral.community.entity.User;
import com.coral.community.service.MessageService;
import com.coral.community.service.UserService;
import com.coral.community.util.CommunityConstant;
import com.coral.community.util.CommunityUtil;
import com.coral.community.util.HostHolder;
import org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;
import org.unbescape.html.HtmlEscape;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    // conversation list
    @RequestMapping(path="/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){

        //Integer.valueOf("abc");

        User user = hostHolder.getUser();

        // Paging
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        // Conversation List
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(),page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if(conversationList != null){
            for(Message message : conversationList){
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));

                // show the header of the message sender
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);

        // count all unread message
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);

        // count all unread notifications
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "/site/letter";
    }


    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model){
        // paging
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // Message List
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if(letterList != null){
            for(Message message : letterList){
                Map<String , Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);

        model.addAttribute("target", getLetterTarget(conversationId));

        // unread0 -> read1
        List<Integer> ids = getLetterIds(letterList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if(hostHolder.getUser().getId() == id0){
            return userService.findUserById(id1);
        }else{
            return userService.findUserById(id0);
        }
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content){
        // Integer.valueOf("abc");
        User target = userService.findUserByName(toName);
        if(target == null){
            return CommunityUtil.getJSONString(1,"Target user doesn't exist !");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }else{
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }

    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if(letterList != null){
            for(Message message : letterList){
                if(hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();

        // 1. query notification of comment
        Message message = messageService.findLatestNotice(user.getId(),TOPIC_COMMENT);
        Map<String, Object> messageVO = new HashMap<>();
        if(message != null){
            messageVO.put("message",message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user",userService.findUserById((Integer)data.get("userId")));
            messageVO.put("entityType",data.get("entityType"));
            messageVO.put("entityId",data.get("entityId"));
            messageVO.put("postId",data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(),TOPIC_COMMENT);
            messageVO.put("count",count);

            int unread = messageService.findNoticeUnreadCount(user.getId(),TOPIC_COMMENT);
            messageVO.put("unread",unread);
        }
        model.addAttribute("commentNotice",messageVO);


        // 2. query notification of like
        message = messageService.findLatestNotice(user.getId(),TOPIC_LIKE);
        messageVO = new HashMap<>();
        if(message != null){
            messageVO.put("message",message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType",data.get("entityType"));
            messageVO.put("entityId",data.get("entityId"));
            messageVO.put("postId",data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(),TOPIC_LIKE);
            messageVO.put("count",count);

            int unread = messageService.findNoticeUnreadCount(user.getId(),TOPIC_LIKE);
            messageVO.put("unread",unread);
        }
        model.addAttribute("likeNotice",messageVO);


        // 3. query notification of follow
        message = messageService.findLatestNotice(user.getId(),TOPIC_FOLLOW);
        messageVO = new HashMap<>();
        if(message != null){
            messageVO.put("message",message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user",userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType",data.get("entityType"));
            messageVO.put("entityId",data.get("entityId"));
            // messageVO.put("postId",data.get("postId")); a follow you,link to a, don't need id

            int count = messageService.findNoticeCount(user.getId(),TOPIC_FOLLOW);
            messageVO.put("count",count);

            int unread = messageService.findNoticeUnreadCount(user.getId(),TOPIC_FOLLOW);
            messageVO.put("unread",unread);
        }
        model.addAttribute("followNotice",messageVO);

        // total unread  notification
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);

        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "/site/notice";
    }

    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model){
        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(),topic));

        List<Message> noticeList = messageService.findNotices(user.getId(),topic,page.getOffset(),page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();

        if(noticeList != null){
            for(Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>();

                // Notice
                map.put("notice", notice);
                // content
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content,HashMap.class);
                map.put("user",userService.findUserById((Integer) data.get("userId")));
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));

                // notice author
                map.put("fromUser", userService.findUserById(notice.getFromId()));

                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices",noticeVoList);

        // unread -> read
        // 1. get list of id need to be set from unread -> read
        List<Integer> ids = getLetterIds(noticeList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";
    }
}
