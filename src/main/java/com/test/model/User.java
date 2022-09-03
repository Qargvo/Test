package com.test.model;

import org.springframework.lang.NonNull;

public record User(
        @NonNull
        String login,
        @NonNull
        String password
) {
}
