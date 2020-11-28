package com.coral.community.service;

import com.coral.community.dao.AlphaDao;
import com.coral.community.dao.DiscussPostMapper;
import com.coral.community.dao.UserMapper;
import com.coral.community.entity.DiscussPost;
import com.coral.community.entity.User;
import com.coral.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;


@Service
@Scope("prototype") // ("singleton")  prototype:instantiate multiple time
public class AlphaService {
    // AlphaService needs dao, thus we injection alphaDao here
    @Autowired
    private AlphaDao alphaDao;
    // then we can use the method (access the db and manipulate the data) from alphaDao
    public String find(){
        return alphaDao.select();
    }

    // " demo for Automatically called method "

    // constructor
    public AlphaService(){
        System.out.println("Instantiate AlphaService");
    }

    @PostConstruct // after dependency injection, automatically call this method, used to initialize some data
    public void init(){
        System.out.println("Initialize AlphaService");
    }
    @PreDestroy // method will be called before destroy
    public void destroy(){
        System.out.println("Destroy AlphaService");
    }

    /*--------------------------------Transaction with spring transactionTemplates------------------------------------*/
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;


    // Propagation (method a might call method b, we need to assign which is more )
    // * REQUIRED : support current transaction (external), create a new one if not exists
    // * REQUIRED_NEW : create a new transaction, and stop the current transaction if exist
    // * NESTED : if current trans is already exist, thus embedded run ( commit and rollback independently), if not, use REQUIRED
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1(){
        // add user
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // add post
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello alpha");
        post.setContent("new comer");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        // error occur
        Integer.valueOf("abc");

        return "ok";
    }

    // Transaction-------Method 2-------TransactionTemplate
    @Autowired
    private TransactionTemplate transactionTemplate;

    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                // add user
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0,5));
                user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
                user.setEmail("alpha@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                // add post
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("Hello BETA");
                post.setContent("new comer");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                // error occur
                Integer.valueOf("abc");

                return "ok";
            }
        });
    }

}
