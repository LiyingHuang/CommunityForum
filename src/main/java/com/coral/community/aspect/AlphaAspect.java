package com.coral.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {

    // 1. PointCut: define pointcut: where to use Advice(Logging/security/Transaction)
    // * all return type is valid
    // com.coral.community.service.*      all class of service package
    // com.coral.community.service.*.*    all method of all class of service package
    // (..)   all argument
    @Pointcut("execution(* com.coral.community.service.*.*(..))")
    public void pointcut(){

    }


    // Advice type :
    // Advice timing

    // before pointcut
    @Before("pointcut()")
    public void before(){
        System.out.println("Before pointcut");
    }
    // After pointcut
    @After("pointcut()")
    public void after(){
        System.out.println("After pointcut");
    }

    // After return
    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("after Returning");
    }

    // AfterThrowing
    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("AfterThrowing");
    }


    @Around("pointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        System.out.println("around before");
        Object obj = proceedingJoinPoint.proceed(); // call the target method
        System.out.println("around after");
        return obj;
    }
}
