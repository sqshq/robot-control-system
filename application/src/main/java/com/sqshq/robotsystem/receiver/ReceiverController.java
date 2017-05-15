package com.sqshq.robotsystem.receiver;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.sqshq.robotsystem.config.spring.SpringProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@Profile("receiver")
public class ReceiverController {

    @Autowired
    private ActorSystem system;

    @RequestMapping(value = "/sensors/data", method = RequestMethod.POST)
    private DeferredResult<String> receiveSensorsData(@RequestBody String data) {
        DeferredResult<String> result = new DeferredResult<>();
        system.actorOf(SpringProps.create(system, ReceiverActor.class, result))
                .tell(Integer.valueOf(data), ActorRef.noSender());
        return result;
    }
}
