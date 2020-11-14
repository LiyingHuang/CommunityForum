package com.coral.community.service;

import com.coral.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


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
}
