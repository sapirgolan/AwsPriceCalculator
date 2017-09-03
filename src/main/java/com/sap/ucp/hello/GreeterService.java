package com.sap.ucp.hello;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class GreeterService {
    public static final String template = "Hello, %s!";
    public final AtomicLong counter = new AtomicLong();

    public GreeterService() {
    }

    public Greeting greet(String name) {
        return new Greeting(counter.getAndIncrement(), String.format(template, name));
    }
}