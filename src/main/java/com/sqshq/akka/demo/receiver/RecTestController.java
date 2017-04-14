package com.sqshq.akka.demo.receiver;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/sensors")
public class RecTestController {

    @Autowired
    private ActorSystem system;

    @RequestMapping(value = "/rectest", method = RequestMethod.POST)
    private DeferredResult<Long> receiveSensorData(@RequestBody String data) {
        DeferredResult<Long> result = new DeferredResult<>();
        system.actorFor("akka.tcp://robot-system@172.18.0.4:2552/user/processorRouter")
                .tell(data, ActorRef.noSender());
        return result;
    }
}