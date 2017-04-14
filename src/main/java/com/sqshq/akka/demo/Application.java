package com.sqshq.akka.demo;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import akka.routing.RoundRobinPool;
import com.sqshq.akka.demo.config.SpringExtension;
import com.sqshq.akka.demo.config.SpringProps;
import com.sqshq.akka.demo.processor.ProcessorActor;
import com.sqshq.akka.demo.transmitter.WebsocketHandler;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@SpringBootApplication
@EnableScheduling
public class Application {

    @Autowired
    private ActorSystem system;

    @EnableWebSocket
    public class WebSocketConfiguration implements WebSocketConfigurer {

        @Autowired
        private WebsocketHandler handler;

        @Override
        public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
            registry
                    .addHandler(handler, "/")
                    .setAllowedOrigins("*");
        }
    }

    @Bean
    public ActorSystem actorSystem(ApplicationContext context) {

        ActorSystem system = ActorSystem.create("robot-system", ConfigFactory.load());
        SpringExtension.getInstance().get(system).initialize(context);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    Cluster cluster = Cluster.get(system);
                    cluster.leave(cluster.selfAddress());
                })
        );

        return system;
    }

    @Bean
    public ActorRef processorRouter() {

        if (!Cluster.get(system).getSelfRoles().contains("processor")) {
            return null;
        }

        return system.actorOf(SpringProps.create(system, ProcessorActor.class)
                .withRouter(new RoundRobinPool(10)), "processorRouter");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
