package com.test.model;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class Token {
    public String generate(){
        String s = UUID.randomUUID().toString();
        return s;
        //String s = login + LocalDate.now().plusDays(1);;
        //return s;
    }
}
