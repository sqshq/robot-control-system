package com.sqshq.robotsystem;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import akka.cluster.metrics.AdaptiveLoadBalancingGroup;
import akka.cluster.metrics.CpuMetricsSelector;
import akka.cluster.routing.ClusterRouterGroup;
import akka.cluster.routing.ClusterRouterGroupSettings;
import akka.routing.RoundRobinPool;
import com.sqshq.robotsystem.config.spring.SpringExtension;
import com.sqshq.robotsystem.config.spring.SpringProps;
import com.sqshq.robotsystem.processor.ProcessorActor;
import com.sqshq.robotsystem.transmitter.WebsocketHandler;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.List;

import static java.util.Collections.singletonList;

@SpringBootApplication
public class Application {

    @Autowired
    private ActorSystem system;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ActorSystem actorSystem(ApplicationContext context) {

        ActorSystem system = ActorSystem.create("robotsystem", ConfigFactory.load());
        SpringExtension.getInstance().get(system).initialize(context);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    Cluster cluster = Cluster.get(system);
                    cluster.leave(cluster.selfAddress());
                })
        );

        return system;
    }

    @Bean("clusterProcessorRouter")
    @Profile("receiver")
    public ActorRef clusterProcessorRouter() {
        List<String> path = singletonList("/user/localProcessorRouter");
        return system.actorOf(new ClusterRouterGroup(new AdaptiveLoadBalancingGroup(CpuMetricsSelector.getInstance(), path),
                        new ClusterRouterGroupSettings(100, path, false, "processor")).props(), "clusterProcessorRouter");
    }

    @Bean("localProcessorRouter")
    @Profile("processor")
    public ActorRef localProcessorRouter() {
        return system.actorOf(SpringProps.create(system, ProcessorActor.class)
                .withDispatcher("processor-dispatcher")
                .withRouter(new RoundRobinPool(10)), "localProcessorRouter");
    }

    @EnableWebSocket
    @Profile("transmitter")
    public class WebSocketConfiguration implements WebSocketConfigurer {

        @Autowired
        private WebsocketHandler handler;

        @Override
        public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
            registry.addHandler(handler, "/robots/socket").setAllowedOrigins("*");
        }
    }
}
