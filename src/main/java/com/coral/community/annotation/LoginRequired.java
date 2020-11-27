package com.coral.community.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
/----------------------- we can also use customized annotation to use Interceptor -------------------------------/
* 1. use Interceptor
*    a. declare customized annotation before the method
*    b. intercept all request, only process methods with annotation
* 2. customized annotation
*    * @Target : declare the active part(method/field/class) of the annotation
*    * @Retention : active time(compile/runtime)
*    * @Document
*    * @Inherited : subclass inherited from parent class with customized annotation, declare inherit the annotation or not
* 3. how to read the annotation (Reflection)
*   * Method.getDeclaredAnnotations()
*   * Method.getAnnotation(Class<T> annotationClass)

* */


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
    /* 1. Figure out what requests can be accessed after Login*/
    /*     a.setting  b.upload */
    /* Need to add annotation in those two methods----UserController*/

    /* 2. define interceptor ---annotation/LoginRequired*/
}
