package com.coral.community.dao;

/*  1. Data Layer(DAO) -> Comment Mapper
         *     * get one page comment data according Entity
         *     * get/find quantity of comment according to entity
*/

import com.coral.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface CommentMapper {
    // get one page comment data according Entity
    // entity_id: specific post/course/comment
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    // get/find count/quantity of comment according to entity
    int selectCountByEntity(int entityType, int entityId);

    // add comment
    int insertComment(Comment comment);

}
