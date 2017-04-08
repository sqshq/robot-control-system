package com.sqshq.akka.demo;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MyActor extends AbstractActor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public MyActor() {
        receive(ReceiveBuilder
                .match(String.class, string -> log.info("Received String message: {}", string))
                .matchAny(object -> log.info("received unknown message"))
                .build()
        );
    }


//    @Scheduled(fixedRate = 100000)
//    public void scheduler() {
//        ActorRef actorRef = actorSystem.actorOf(SpringExtension.SpringExtProvider.get(actorSystem).props(MyActor.class));
//        actorRef.tell("HELLO! " + UUID.randomUUID(), ActorRef.noSender());
//    }
}
