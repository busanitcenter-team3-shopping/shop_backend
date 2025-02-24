package com.secure.shopbackend.security.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {
    private String jwtToken;
    private String name;
    private String email;

    public LoginResponse(String jwtToken, String name, String email) {
        this.jwtToken = jwtToken;
        this.name = name;
        this.email = email;
    }
}
