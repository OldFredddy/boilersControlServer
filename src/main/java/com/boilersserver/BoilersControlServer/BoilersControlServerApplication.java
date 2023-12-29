package com.boilersserver.BoilersControlServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BoilersControlServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoilersControlServerApplication.class, args);

	}

}
