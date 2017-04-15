package com.sqshq.akka.demo.processor;

import akka.actor.AbstractActor;
import com.sqshq.akka.demo.config.Actor;
import com.sqshq.akka.demo.processor.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Actor
public class ProcessorActor extends AbstractActor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProcessorService processorService;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::process)
                .matchAny(this::unhandled)
                .build();
    }

    private void process(Object data) {
        log.info("Processor: {}", data);
        processorService.compute();
    }
}
