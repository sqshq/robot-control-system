package com.sqshq.akka.demo.receiver;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSubMediator;
import com.sqshq.akka.demo.config.Actor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.request.async.DeferredResult;

@Actor
public class ReceiverActor extends AbstractActor {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final DeferredResult<Long> deferredResult;

    @Autowired
    @Qualifier("clusterProcessorRouter")
    private ActorRef router;

    @Autowired
    @Qualifier("pubSubMediator")
    private ActorRef mediator;

    public ReceiverActor(DeferredResult<Long> deferredResult) {
        this.deferredResult = deferredResult;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::dispatch)
                .match(Long.class, this::complete)
                .matchAny(this::unhandled)
                .build();
    }

    private void dispatch(String data) {
        log.info("Receiver: {}", data);

        mediator.tell(new DistributedPubSubMediator.Publish("1", data), self());
        router.tell(data, self());

        deferredResult.setResult(Long.MAX_VALUE);
    }

    private void complete(Long result) {
        deferredResult.setResult(result);
    }
}
