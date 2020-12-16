package com.coral.community.controller;

import com.coral.community.entity.DiscussPost;
import com.coral.community.entity.Page;
import com.coral.community.service.ElasticsearchService;
import com.coral.community.service.LikeService;
import com.coral.community.service.UserService;
import com.coral.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    // search?keyword=xxx
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) {
        // search post
        org.springframework.data.domain.Page<DiscussPost> searchResult =
        elasticsearchService.searchDiscussPost(keyword, page.getCurrent()-1, page.getLimit());

        // Aggregate data
        List<Map<String, Object>> discussPost = new ArrayList<>();
        if (searchResult != null){
            for(DiscussPost post : searchResult){
                Map<String, Object> map = new HashMap<>();

                // post
                map.put("post",post);
                map.put("user",userService.findUserById(post.getUserId()));
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPost.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPost);
        model.addAttribute("keyword",keyword);

        // paging
        page.setPath("/search?keyword="+keyword);
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());

        return "/site/search";
    }
}
