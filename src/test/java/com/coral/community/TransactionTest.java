package com.coral.community;

import com.coral.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTest {

    @Autowired
    private AlphaService alphaService;

    @Test
    public void testSave1(){
        Object obj = alphaService.save1();
        System.out.println(obj);
        // there is an error,
        // the transaction should be rollback, so user and post shouldn't be in the database
    }

    @Test
    public void testSave2(){
        Object obj = alphaService.save2();
        System.out.println(obj);
        // there is an error,transaction should be rollback, so user and post shouldn't be in the database
    }
}
/*
*-----------------------------------add comment----------------------------------
*  1. Data Layer(DAO) -> Comment Mapper
*     * add comment data
*     * modify the quantity of the comment
 * 2. Service Layer
 *    * add comment service
 *    * add the comment, then update the quantity of the comment (with Transaction)
 * 3. Controller Layer
 *    * handle the request of adding comment
 *    * set the table of the comment
* */

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