package com.sqshq.akka.demo.receiver;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

@Component
@Scope("prototype")
public class ReceiverActor extends AbstractActor {

    @Autowired
    private ActorRef processorRouter;

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final DeferredResult<Long> deferredResult;

    public ReceiverActor(DeferredResult<Long> deferredResult) {
        this.deferredResult = deferredResult;
        receive(ReceiveBuilder
                .match(String.class, this::dispatch)
                .match(Long.class, this::complete)
                .matchAny(this::unhandled)
                .build()
        );
    }

    private void dispatch(String data) {
        log.info("Received String message: {}", data);
        processorRouter.tell(data, self());
        deferredResult.setResult(Long.MAX_VALUE);
    }

    private void complete(Long result) {
        deferredResult.setResult(result);
    }
}
