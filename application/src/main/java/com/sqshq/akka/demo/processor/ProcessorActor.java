package com.sqshq.akka.demo.processor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSubMediator;
import com.sqshq.akka.demo.config.Actor;
import com.sqshq.akka.demo.processor.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Random;

@Actor
public class ProcessorActor extends AbstractActor {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Random random = new Random();

    @Autowired
    @Qualifier("pubSubMediator")
    private ActorRef mediator;

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
        Integer computedValue = processorService.compute(random.nextInt(1000) + 2000);
        mediator.tell(new DistributedPubSubMediator.Publish("1", data), self());
        sender().tell(computedValue, self());
    }
}
