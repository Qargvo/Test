package com.test.model;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class Token {
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
