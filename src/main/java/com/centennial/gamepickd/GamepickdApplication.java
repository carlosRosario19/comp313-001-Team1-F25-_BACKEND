package com.centennial.gamepickd;

import com.centennial.gamepickd.config.StorageProperties;
import com.centennial.gamepickd.security.RsaKeyProperties;
import com.centennial.gamepickd.services.contracts.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({RsaKeyProperties.class, StorageProperties.class})
@SpringBootApplication
public class GamepickdApplication {

	public static void main(String[] args) {
		SpringApplication.run(GamepickdApplication.class, args);
	}

}
