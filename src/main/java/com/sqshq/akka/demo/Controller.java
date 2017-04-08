package com.sqshq.akka.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hi")
public class Controller {

    @RequestMapping("/there")
    private String hi() {
        return "hi!";
    }
}
