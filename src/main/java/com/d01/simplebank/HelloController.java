package com.d01.simplebank;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HelloController {
    @GetMapping("/")
    public Map<String, String> hello() {
        return Map.of("result", "hello, world");
    }
} 