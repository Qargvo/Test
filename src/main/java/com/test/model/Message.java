package com.test.model;

import org.springframework.lang.NonNull;

public record Message(
        @NonNull
        String login,
        @NonNull
        String message
) {
}
