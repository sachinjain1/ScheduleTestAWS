package com.test.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author sachin
 *
 */
@SpringBootApplication
@EnableScheduling
public class TestSchedule {

	public static void main(String[] args) {
		SpringApplication.run(TestSchedule.class, args);
		System.out.println("Yes !! We got it running");
	}
	

}

