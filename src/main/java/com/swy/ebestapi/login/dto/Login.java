package com.swy.ebestapi.login.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Login {
    String grant_type = "client_credentials";
    String appKey = "";
    String secretKey = "";
    String scope = "oob";
    String token = "";
}
