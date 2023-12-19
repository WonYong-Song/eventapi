package com.swy.stockapi.login.service;

import org.springframework.stereotype.Service;

import com.swy.stockapi.login.dto.Login;
import com.swy.stockapi.util.EbestHttpClientManager;
import com.swy.stockapi.util.HantooHttpClientManager;
import com.swy.stockapi.util.SessionManager;

import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService{
    private final SessionManager sm;
    private final EbestHttpClientManager ehcm;
    private final HantooHttpClientManager hhcm;
    @Override
    public boolean loginProcess(Login info, HttpServletResponse res) {
        
        boolean loginResult = false;
        if (info.getGuboon().equals("ebest")) {
            loginResult = ehcm.tryLogin(info);
        } else if (info.getGuboon().equals("hantoo")) {
            loginResult = hhcm.tryLogin(info);
        }
        if (loginResult) {
            log.debug("login success, token : " +info.getToken());
            sm.createSession(info, res);
            return true;
        } else {
            return false;
        }
    }
    
}
