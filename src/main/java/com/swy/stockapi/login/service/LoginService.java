package com.swy.stockapi.login.service;

import javax.servlet.http.HttpServletResponse;

import com.swy.stockapi.login.dto.Login;

public interface LoginService {
    public boolean loginProcess(Login info, HttpServletResponse req);
}
