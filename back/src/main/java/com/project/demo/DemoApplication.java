package com.project.demo;

import com.project.demo.property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

/*
*
* @EnableConfigurationProperties -> 使 @ConfigurationProperties 該註解變為有效
* @ConfigurationProperties -> 將application.yml轉換為bean來使用
* */
@SpringBootApplication
@EnableJdbcHttpSession
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


}
