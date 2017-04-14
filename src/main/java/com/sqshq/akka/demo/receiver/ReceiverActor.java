package com.sqshq.akka.demo.receiver;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.async.DeferredResult;

//@Actor
public class ReceiverActor extends AbstractActor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final DeferredResult<Long> deferredResult;

    private final ActorRef mediator;
    private final ActorRef router;

    public ReceiverActor(DeferredResult<Long> deferredResult, ActorRef router) {
        this.deferredResult = deferredResult;
        this.router = router;

//        List<String> path = singletonList("/user/processorRouter");
//        AdaptiveLoadBalancingGroup routerGroup = new AdaptiveLoadBalancingGroup(MixMetricsSelector.getInstance(), path);
//        ClusterRouterGroupSettings settings = new ClusterRouterGroupSettings(100, path, false, "processor");
//        ClusterRouterGroup group = new ClusterRouterGroup(routerGroup, settings);
//        router = getContext().actorOf(group.props(), "myservicetype-router");

//        router = context().actorOf(
//                new ClusterRouterGroup(new BroadcastGroup(path),
//                        new ClusterRouterGroupSettings(100, path, false, "processor")).props(), "clusterProcessorRouter");

        mediator = DistributedPubSub.get(
                getContext().system()).mediator();
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
