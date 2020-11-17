package com.coral.community;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTest {
    // different class use different Loggger
    private static final Logger logger = LoggerFactory.getLogger(LoggerTest.class); // Logger name

    @Test
    public void testLogger(){
        System.out.println();
        logger.debug("Debug Log");
        logger.info("Info Log");
        logger.warn("Warn Log");
        logger.error("Error Log");
    }
}
