package com.event.scope.eventScope;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class EventScopeApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventScopeApplication.class, args);
	}

}
