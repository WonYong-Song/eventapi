package com.swy.ebestapi.login.service;

import org.springframework.stereotype.Service;

import com.swy.ebestapi.login.dto.Login;
import com.swy.ebestapi.util.HttpClientManager;
import com.swy.ebestapi.util.SessionManager;

import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService{
    private final SessionManager sm;
    private final HttpClientManager hcm;
    @Override
    public boolean loginProcess(Login info, HttpServletResponse req) {
        
        sm.createSession(info, req);
        if (hcm.tryLogin(info)) {
            System.out.println("login success, token : " +info.getToken());
            return true;
        } else {
            return false;
        }
    }
    
}
