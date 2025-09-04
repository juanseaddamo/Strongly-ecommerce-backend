package com.uade.tpo.demo.entity.dto;

import lombok.Data;
@Data
public class UserRequest {
    private String fullName;
    private String email;
    private String password;

    public UserRequest() {
    }

    public UserRequest(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

}
