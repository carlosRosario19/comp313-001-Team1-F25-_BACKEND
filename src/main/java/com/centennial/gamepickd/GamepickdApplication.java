package com.centennial.gamepickd;

import com.centennial.gamepickd.security.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({RsaKeyProperties.class})
@SpringBootApplication
public class GamepickdApplication {

	public static void main(String[] args) {
		SpringApplication.run(GamepickdApplication.class, args);
	}

}
