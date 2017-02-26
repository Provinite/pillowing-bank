package com.clovercoin.pillowing;

import com.clovercoin.pillowing.entity.Role;
import com.clovercoin.pillowing.entity.User;
import com.clovercoin.pillowing.repository.RoleRepository;
import com.clovercoin.pillowing.repository.UserRepository;
import com.clovercoin.pillowing.service.RoleService;
import com.clovercoin.pillowing.service.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.security.Security;
import java.util.HashSet;

@SpringBootApplication
@Log
public class PillowingBankApplication {
	private static String adminEmail;
	private static String adminPassword;

	@Value("${com.clovercoin.pillowing.admin-password}")
	private void setAdminPassword(String adminPassword) {
		PillowingBankApplication.adminPassword = adminPassword;
	}

	@Value("${com.clovercoin.pillowing.admin-email}")
	private void setAdminEmail(String adminEmail) {
		PillowingBankApplication.adminEmail = adminEmail;
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(PillowingBankApplication.class, args);

		RoleService roleService = context.getBean(RoleService.class);
		UserService userService = context.getBean(UserService.class);

		Assert.notNull(roleService, "The application context must provide a RoleService");
		Assert.notNull(userService, "The application context must provide a UserService");

		Role adminRole = new Role(SecurityConfiguration.ADMIN_ROLE);
		Role clientRole = new Role(SecurityConfiguration.CLIENT_ROLE);
		Role modRole = new Role(SecurityConfiguration.MOD_ROLE);
		Role guestArtistRole = new Role(SecurityConfiguration.GUEST_ARTIST_ROLE);

		log.info("Creating ADMIN role: " + adminRole.getRole());
		roleService.createRoleIfNotExists(adminRole);

		log.info("Creating CLIENT role: " + clientRole.getRole());
		roleService.createRoleIfNotExists(clientRole);

		log.info("Creating MOD role: " + modRole.getRole());
		roleService.createRoleIfNotExists(modRole);

		log.info("Creating GUEST_ARTIST role: " + guestArtistRole.getRole());
		roleService.createRoleIfNotExists(guestArtistRole);

		if (userService.findUserByEmail(adminEmail) == null) {
			Assert.notNull(adminEmail);
			Assert.notNull(adminPassword);

			log.info("Creating default user - " + adminEmail + " / " + adminPassword);
			User user = new User();
			user.setActive(1);
			user.setEmail(adminEmail);
			user.setUsername("CCAdmin");
			user.setPassword(adminPassword);
			userService.addRole(user, SecurityConfiguration.ADMIN_ROLE);
			userService.createUser(user);
		}

	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		log.info("Configuring CORS");
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**");
			}
		};
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder;
	}
}
