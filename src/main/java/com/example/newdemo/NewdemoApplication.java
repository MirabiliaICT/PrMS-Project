package com.example.newdemo;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Theme("custom-style")
@SpringBootApplication(scanBasePackages = {"boot.registration"} , exclude = {SecurityAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "com.example.newdemo.Repository")
@ComponentScan(basePackages = "com.example.newdemo.Service")
public class NewdemoApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		System.out.println("PMS");
		SpringApplication.run(NewdemoApplication.class, args);
	}

}
