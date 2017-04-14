package com.sqshq.akka.demo.transmitter;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import com.sqshq.akka.demo.config.SpringProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebsocketHandler extends TextWebSocketHandler {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ActorSystem system;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("new robot connected via websocket: {}", session.getId());
        session.getAttributes().put("actor",
                system.actorOf(SpringProps.create(system, RobotActor.class, session)));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ActorRef actor = (ActorRef) session.getAttributes().get("actor");
        actor.tell(PoisonPill.getInstance(), ActorRef.noSender());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("robot {} disconnected from websocket", session.getId());
        ActorRef actor = (ActorRef) session.getAttributes().get("actor");
        actor.tell(message, ActorRef.noSender());
    }
}
