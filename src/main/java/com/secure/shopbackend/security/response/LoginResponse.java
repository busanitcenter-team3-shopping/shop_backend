package com.secure.shopbackend.security.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {
    private String jwtToken;
    private String email;

    public LoginResponse(String jwtToken, String email) {
        this.jwtToken = jwtToken;
        this.email = email;
    }
}
