package com.sqshq.akka.demo;

import akka.actor.ActorSystem;
import akka.cluster.Cluster;
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

        ActorSystem system = ActorSystem.create("robot-system", ConfigFactory.load());
        SpringExtension.SpringExtProvider.get(system).initialize(applicationContext);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    Cluster cluster = Cluster.get(system);
                    cluster.leave(cluster.selfAddress());
                })
        );

        return system;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
