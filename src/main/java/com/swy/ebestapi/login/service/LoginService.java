package com.swy.ebestapi.login.service;

import com.swy.ebestapi.login.dto.Login;

import javax.servlet.http.HttpServletResponse;

public interface LoginService {
    public boolean loginProcess(Login info, HttpServletResponse req);
}
