package com.sqshq.akka.demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import akka.cluster.routing.ClusterRouterGroup;
import akka.cluster.routing.ClusterRouterGroupSettings;
import akka.routing.ConsistentHashingGroup;
import com.sqshq.akka.demo.config.SpringExtension;
import com.sqshq.akka.demo.processor.ProcessorActor;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

import static java.util.Collections.singletonList;

@SpringBootApplication
@EnableScheduling
public class Application {

    @Autowired
    private ActorSystem system;

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

    //TODO custom qualifier
    @Bean
    public ActorRef processorRouter() {

        if (!Cluster.get(system).getSelfRoles().contains("processor")) {
            return null;
        }

        List<String> path = singletonList("/user/" + ProcessorActor.class.getSimpleName());

        return system.actorOf(
                new ClusterRouterGroup(new ConsistentHashingGroup(path),
                        new ClusterRouterGroupSettings(10, path, true, "processor")).props(), "processorRouter");

//        return system.actorOf(
//                new ClusterRouterPool(new RoundRobinPool(100),
//                        new ClusterRouterPoolSettings(1000, 100,
//                                true, "processor")).props(SpringExtension.SpringExtProvider.get(system).props(ProcessorActor.class)));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
