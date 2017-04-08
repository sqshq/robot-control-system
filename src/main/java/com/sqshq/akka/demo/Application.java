package com.sqshq.akka.demo;

import akka.actor.ActorSystem;
import com.sqshq.akka.demo.config.SpringExtension;
import com.typesafe.config.ConfigFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {

    @Bean
    public ActorSystem actorSystem(ApplicationContext applicationContext) {
        ActorSystem actorSystem = ActorSystem.create("robot-system", ConfigFactory.load());
        SpringExtension.SpringExtProvider.get(actorSystem).initialize(applicationContext);
        return actorSystem;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
