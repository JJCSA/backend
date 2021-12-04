package com.jjcsa;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class JjcsaApplication {

	public static void main(String[] args) {
		SpringApplication.run(JjcsaApplication.class, args);
	}

	@RestController
	class TestController {
		@Value("${test.variable:local-value}")
		private String testVariable;

		@GetMapping("/test/param-store")
		public String getTestVariable() {
			return testVariable;
		}
	}
}
