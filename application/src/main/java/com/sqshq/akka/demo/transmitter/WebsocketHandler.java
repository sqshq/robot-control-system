package com.sqshq.akka.demo.transmitter;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import com.sqshq.akka.demo.config.spring.SpringProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class WebsocketHandler extends TextWebSocketHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AtomicLong robotCount = new AtomicLong();

    @Autowired
    private ActorSystem system;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("new robot connected via websocket: {}", session.getId());
        session.getAttributes().put("actor", system.actorOf(
                SpringProps.create(system, RobotActor.class, session, robotCount.incrementAndGet())));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ActorRef actor = (ActorRef) session.getAttributes().get("actor");
        actor.tell(PoisonPill.getInstance(), ActorRef.noSender());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.debug("robot {} has been disconnected from websocket", session.getId());
        ActorRef actor = (ActorRef) session.getAttributes().get("actor");
        actor.tell(message, ActorRef.noSender());
    }
}
