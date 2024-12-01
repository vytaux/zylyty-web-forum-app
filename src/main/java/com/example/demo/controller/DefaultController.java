package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class DefaultController {

    @GetMapping
    public String home() {
        return "Hello World!<br>This is a homepage for a Web Forum App JSON-based API.<br>You can find the documentation at <a href=\"https://github.com/vytaux/zylyty-web-forum-app\">https://github.com/vytaux/zylyty-web-forum-app</a>.";
    }
}
