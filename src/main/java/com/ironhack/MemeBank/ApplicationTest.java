package com.ironhack.MemeBank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("test")
public class ApplicationTest {
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(ApplicationTest.class);
    app.setLogStartupInfo(false);
    app.run(args);
  }
}
