package com.thejas.ca_billing_system.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
    private String email; // only used for register
}
