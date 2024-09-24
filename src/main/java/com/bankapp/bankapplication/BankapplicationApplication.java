package com.bankapp.bankapplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Java Bank Application",
				description = "Backend Rest APIs for Bank Application",
				version = "v1.0",
				contact = @Contact(
						name = "Tinashe Nyenge",
						email = "tbnyenge@gmail.com",
						url = "https://github.com/nyenge/bank_application"
				),
				license = @License(
						name = "Java Bank Application",
						url = "https://github.com/nyenge/bank_application"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "Java Bank Application documentation",
				url = "https://github.com/nyenge/bank_application"
		)
)
@EnableFeignClients
@EnableJpaRepositories
public class BankapplicationApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankapplicationApplication.class, args);
	}
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		return restTemplate;
	}

}
