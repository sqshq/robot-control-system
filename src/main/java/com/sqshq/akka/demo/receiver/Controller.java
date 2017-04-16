package com.sqshq.akka.demo.receiver;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.sqshq.akka.demo.config.spring.SpringProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class Controller {

    @Autowired
    private ActorSystem system;

    @RequestMapping(value = "/sensors/data", method = RequestMethod.POST)
    private DeferredResult<Integer> receiveSensorsData(@RequestBody String data) {
        DeferredResult<Integer> result = new DeferredResult<>();
        system.actorOf(SpringProps.create(system, ReceiverActor.class, result))
                .tell(data, ActorRef.noSender());
        return result;
    }
}
