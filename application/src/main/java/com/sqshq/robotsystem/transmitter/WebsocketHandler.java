package com.sqshq.robotsystem.transmitter;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.cluster.Cluster;
import akka.cluster.ddata.DistributedData;
import akka.cluster.ddata.Key;
import akka.cluster.ddata.PNCounter;
import akka.cluster.ddata.PNCounterKey;
import akka.cluster.ddata.Replicator;
import com.sqshq.robotsystem.config.Counters;
import com.sqshq.robotsystem.config.spring.SpringProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class WebsocketHandler extends TextWebSocketHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Key<PNCounter> clusterRobotsCounter = PNCounterKey.create(Counters.SUBSCRIBED_ROBOTS.name());
    private final AtomicInteger localRobotsCounter = new AtomicInteger();
    private final ActorRef replicator;
    private final ActorSystem system;

    public WebsocketHandler(ActorSystem system) {
        this.system = system;
        this.replicator = DistributedData.get(system).replicator();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("new robot connected via websocket: {}", session.getId());
        replicator.tell(new Replicator.Update<>(clusterRobotsCounter, PNCounter.create(),
                Replicator.writeLocal(), counter -> counter.increment(Cluster.get(system), 1)), ActorRef.noSender());
        session.getAttributes().put("actor", system.actorOf(
                SpringProps.create(system, RobotActor.class, session, localRobotsCounter.incrementAndGet())));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ActorRef actor = (ActorRef) session.getAttributes().get("actor");
        actor.tell(PoisonPill.getInstance(), ActorRef.noSender());
        replicator.tell(new Replicator.Update<>(clusterRobotsCounter, PNCounter.create(),
                Replicator.writeLocal(), counter -> counter.decrement(Cluster.get(system), 1)), ActorRef.noSender());
        localRobotsCounter.decrementAndGet();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.debug("robot {} has been disconnected from websocket", session.getId());
        ActorRef actor = (ActorRef) session.getAttributes().get("actor");
        actor.tell(message, ActorRef.noSender());
    }
}
