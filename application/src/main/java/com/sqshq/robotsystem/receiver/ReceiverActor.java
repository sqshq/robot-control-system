package com.sqshq.robotsystem.receiver;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import com.sqshq.robotsystem.config.Actor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.request.async.DeferredResult;

@Actor
public class ReceiverActor extends AbstractActor {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final DeferredResult<Integer> deferredResult;

    @Autowired
    @Qualifier("clusterProcessorRouter")
    private ActorRef router;

    public ReceiverActor(DeferredResult<Integer> deferredResult) {
        this.deferredResult = deferredResult;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::dispatch)
                .match(Integer.class, this::complete)
                .matchAny(this::unhandled)
                .build();
    }

    private void dispatch(String data) {
        log.info("Receiver: {}", data);
        router.tell(Integer.valueOf(data), self());
    }

    private void complete(Integer result) {
        deferredResult.setResult(result);
    }
}
