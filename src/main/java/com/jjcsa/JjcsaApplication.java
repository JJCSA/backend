package com.jjcsa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="com.jjcsa.main")
public class JjcsaApplication {

	public static void main(String[] args) {
		SpringApplication.run(JjcsaApplication.class, args);
	}

}
