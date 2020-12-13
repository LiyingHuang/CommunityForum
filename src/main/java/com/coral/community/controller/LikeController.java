package com.coral.community.controller;

import com.coral.community.annotation.LoginRequired;
import com.coral.community.entity.Comment;
import com.coral.community.entity.DiscussPost;
import com.coral.community.entity.Event;
import com.coral.community.entity.User;
import com.coral.community.event.EventProducer;
import com.coral.community.service.LikeService;
import com.coral.community.util.CommunityConstant;
import com.coral.community.util.CommunityUtil;
import com.coral.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;


    @LoginRequired
    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId){
        User user = hostHolder.getUser();

        // like
        likeService.like(user.getId(), entityType, entityId, entityUserId);

        // count
        long likeCount = likeService.findEntityLikeCount(entityType,entityId);

        // status
        int likeStatus = likeService.findEntityLikeStatus(user.getId(),entityType,entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        // Trigger the like(Topic) event to send the notification
        // "who" likes "the specific comment" of "the post" (need the postId)
        // "who" likes "the post"
        if(likeStatus == 1){  // like(status=1)->Notification, not like->no Notification
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);

            eventProducer.fireEvent(event);
        }


        return CommunityUtil.getJSONString(0,null,map);

    }
}


/*
*   FOLLOW
*   Requirements:
*                   * follow/ not follow function
*                   * count user's followed / follower
*   Key:
*        * A follow B, A is the follower of B, B is the followee of A
*        * Follow target can be User/Post/, we need to convert the target to Entity
* */
