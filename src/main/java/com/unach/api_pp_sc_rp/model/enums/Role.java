package com.unach.api_pp_sc_rp.model.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    STUDENT,
    ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
