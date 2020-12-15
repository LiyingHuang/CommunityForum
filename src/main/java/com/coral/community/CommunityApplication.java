package com.coral.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication   // indicate this is a config file
public class CommunityApplication {

	@PostConstruct
	public void init(){
		// solve the elasticsearch start Netty conflict
		// Netty4Utils.setAvailableProcessors()
		System.setProperty("es.set.netty.runtime.available.processors","false");
	}


	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}

}

/*
*  1. when we run the main method, springboot help us start the tomcat sever in the bottom
*  2. spring boot contains a tomcat jar package, we use this embedded tomcat
*  3. tomcat: sever
**/



