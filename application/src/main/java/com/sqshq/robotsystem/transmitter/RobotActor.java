package com.sqshq.robotsystem.transmitter;

import akka.actor.AbstractActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import com.sqshq.robotsystem.config.Actor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Actor
public class RobotActor extends AbstractActor {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final WebSocketSession session;
    private final Integer robotId;

    public RobotActor(WebSocketSession session, Integer robotId) {
        this.session = session;
        this.robotId = robotId;
        DistributedPubSub.get(getContext().system())
                .mediator()
                .tell(new DistributedPubSubMediator.Subscribe(
                        robotId.toString(), getSelf()), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(TextMessage.class, this::processMessageFromRobot)
                .match(Integer.class, this::sendDataToRobot)
                .match(DistributedPubSubMediator.SubscribeAck.class, this::processSubscription)
                .matchAny(this::unhandled)
                .build();
    }

    private void processMessageFromRobot(TextMessage message) {
        log.info("received message from robot {}: {}", robotId, message);
    }

    private void sendDataToRobot(Integer data) throws IOException {
        log.info("sending message to robot {}: {}", robotId, data);
        session.sendMessage(new TextMessage(data.toString()));
    }

    private void processSubscription(DistributedPubSubMediator.SubscribeAck ack) {
        log.info("robot #{} sucessfully subscribed", robotId);
    }
}
