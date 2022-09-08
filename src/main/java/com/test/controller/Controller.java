package com.test.controller;

import com.test.dataBase.Repository;
import com.test.model.User;
import lombok.RequiredArgsConstructor;
import com.test.model.Message;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class Controller {
    private final Repository repository;

    @PostMapping
    public Map<String, String> newUser(@RequestBody User user) {
        String token = repository.newToken(user);
        return Collections.singletonMap("token", token);
    }

    @PostMapping(headers = "token")
    public Map<String, Boolean> newUser(@RequestHeader("token") String token, @RequestBody Message message) {
        Boolean b = repository.newMessage(token, message);
        return Collections.singletonMap("result", b);
    }

    @GetMapping
    public List<Map<String, Object>> getHistory(@RequestHeader("token") String token, @RequestBody Message message) {
        return repository.getHistory(token, message);
    }

}
