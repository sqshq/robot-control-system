package com.sqshq.akka.demo.receiver;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.sqshq.akka.demo.config.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/sensors")
public class Controller {

    @Autowired
    private ActorSystem system;

    @RequestMapping(value = "/data", method = RequestMethod.POST)
    private DeferredResult<Long> receiveSensorData(@RequestBody String data) {
        DeferredResult<Long> result = new DeferredResult<>();
        system.actorOf(SpringExtension.SpringExtProvider
                .get(system).props(ReceiverActor.class, result))
                .tell(data, ActorRef.noSender());
        return result;
    }
}
