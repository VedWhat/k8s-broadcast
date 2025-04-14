package com.k8sbroadcast.listener.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ListenerController {

    @Value("${HOSTNAME:unknown}")
    private String hostname;

    @GetMapping("/ping")
    public String ping() {
        return "Pong from pod: " + hostname;
    }
}
