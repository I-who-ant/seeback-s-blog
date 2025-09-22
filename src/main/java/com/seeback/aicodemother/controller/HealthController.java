package com.seeback.aicodemother.controller;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RequestMapping("/health")
@RestController
public class HealthController {
    @GetMapping("/check")
    public String check() {
        return "ok666";
    }
}
