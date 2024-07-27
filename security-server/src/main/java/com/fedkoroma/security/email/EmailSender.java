package com.fedkoroma.security.email;

public interface EmailSender {
    void send(String to, String name, String link);
}
