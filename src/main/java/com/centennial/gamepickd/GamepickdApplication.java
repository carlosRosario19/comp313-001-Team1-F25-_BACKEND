package com.centennial.gamepickd;

import com.centennial.gamepickd.config.StorageProperties;
import com.centennial.gamepickd.security.RsaKeyProperties;
import com.centennial.gamepickd.services.contracts.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableConfigurationProperties({RsaKeyProperties.class, StorageProperties.class})
@SpringBootApplication
@EnableJpaAuditing
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableCaching
public class GamepickdApplication {

	public static void main(String[] args) {
		SpringApplication.run(GamepickdApplication.class, args);
	}

}
