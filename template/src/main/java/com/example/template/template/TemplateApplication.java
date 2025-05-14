package com.example.template.template;

import com.example.template.template.model.User;
import com.example.template.template.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TemplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(TemplateApplication.class, args);
	}

	@Bean
	public CommandLineRunner demoData(UserService userService, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
		return args -> {
			// Create or update a default admin user
			User adminUser = userService.findByUsername("admin");
			if (adminUser == null) {
				adminUser = new User("admin", passwordEncoder.encode("password"), "ADMIN");
				userService.saveUser(adminUser);
				System.out.println("Created default admin user: admin/password");
			} else {
				// Update password if user already exists
				adminUser.setPassword(passwordEncoder.encode("password")); // Encode and set the password
				userService.saveUser(adminUser);
				System.out.println("Updated default admin user password: admin/password");
			}
		};
	}
}
