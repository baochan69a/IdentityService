package com.chanas.hello_spring_boot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloSpringController {
    @GetMapping("/hello")
    String sayhello(){
        return "Hello spring boot 3, aggain";
    }
}
