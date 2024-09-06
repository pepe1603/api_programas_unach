package com.unach.api_pp_sc_rp.events;

import org.springframework.context.ApplicationEvent;

public class UserLoginEvent extends ApplicationEvent {
    private final String username;
    private final String email;

    public UserLoginEvent(Object source, String username, String email) {
        super(source);
        this.username = username;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}
