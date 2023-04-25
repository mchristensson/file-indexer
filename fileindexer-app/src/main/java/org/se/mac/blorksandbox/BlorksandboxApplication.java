package org.se.mac.blorksandbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main invocation point
 * Use <code>--spring.profiles.active=dev</code> when launching in IDE (like Intellij)
 */
@SpringBootApplication
public class BlorksandboxApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlorksandboxApplication.class, args);
	}

}
