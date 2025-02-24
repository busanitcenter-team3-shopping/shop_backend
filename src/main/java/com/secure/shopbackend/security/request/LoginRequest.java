package com.secure.shopbackend.security.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    private String email;
    private String name;
    private String password;
}
