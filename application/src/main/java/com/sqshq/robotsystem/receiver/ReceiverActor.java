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
    private final DeferredResult<String> deferredResult;

    @Autowired
    @Qualifier("clusterProcessorRouter")
    private ActorRef router;

    public ReceiverActor(DeferredResult<String> deferredResult) {
        this.deferredResult = deferredResult;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Integer.class, this::dispatch)
                .match(String.class, this::complete)
                .matchAny(this::unhandled)
                .build();
    }

    private void dispatch(Integer data) {
        log.info("receiver dispatching the data: {}", data);
        router.tell(data, self());
    }

    private void complete(String result) {
        log.info("receiver responding to sender: {}", result);
        deferredResult.setResult(result);
        getContext().stop(self());
    }
}
