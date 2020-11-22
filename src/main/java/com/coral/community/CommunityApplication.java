package com.coral.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication   // indicate this is a config file
public class CommunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}

}

/*
*  1. when we run the main method, springboot help us start the tomcat sever in the bottom
*  2. spring boot contains a tomcat jar package, we use this embedded tomcat
*  3. tomcat: sever
**/



