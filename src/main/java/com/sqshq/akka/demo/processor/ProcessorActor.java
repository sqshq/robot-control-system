package com.sqshq.akka.demo.processor;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import com.sqshq.akka.demo.processor.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ProcessorActor extends AbstractActor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProcessorService service;

    public ProcessorActor() {
        receive(ReceiveBuilder
                .match(String.class, this::process)
                .matchAny(this::unhandled)
                .build()
        );
    }

    private void process(String data) {
        log.info("Processor actor received: {}", data);
        service.compute();
    }
}
