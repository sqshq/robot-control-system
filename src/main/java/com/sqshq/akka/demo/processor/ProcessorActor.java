package com.sqshq.akka.demo.processor;

import akka.actor.AbstractActor;
import com.sqshq.akka.demo.config.Actor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Actor
public class ProcessorActor extends AbstractActor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::process)
                .matchAny(this::unhandled)
                .build();
    }

    private void process(Object data) {
        log.info("Processor: {}", data);
    }
}
