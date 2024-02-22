 package com.tui.github;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.tui")
public class GithubApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubApplication.class, args);
	}
}
