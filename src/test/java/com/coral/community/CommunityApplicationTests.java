package com.coral.community;

import com.coral.community.dao.AlphaDao;
import com.coral.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)  // wish to have the same the config/bean with CommunityApplication
class CommunityApplicationTests implements ApplicationContextAware { // ApplicationContextAware inherited from beanFactory

	private ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext; // record the applicationContext for later use
	}

    // get bean through  applicationContext
	@Test
	void ApplicationContextTest() {
		System.out.println(applicationContext);  // sout the spring/bean container

		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class); // dependent on interface AlphaDao rather than implementation
		System.out.println(alphaDao.select());

		alphaDao = applicationContext.getBean("AlphaHibernate", AlphaDao.class);  // access by bean name
		System.out.println(alphaDao.select());
	}

	@Test
	public void testBeanManagement(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);

		alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService); // same with the above one, indicate spring use 'singleton' pattern default
	}

	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat =
				applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

	// use injection
	@Autowired
	@Qualifier("AlphaHibernate")
	private AlphaDao alphaDao;
	@Autowired
	private AlphaService alphaService;
	@Autowired
	private SimpleDateFormat simpleDateFormat;

	@Test
	public void testDI(){
		System.out.println(alphaDao);
		System.out.println(alphaService);
		System.out.println(simpleDateFormat);

	}
}

