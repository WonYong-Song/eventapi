package com.swy.stockapi.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.swy.stockapi.login.dto.Login;
import com.swy.stockapi.login.service.LoginService;
import com.swy.stockapi.util.SessionManager;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequiredArgsConstructor
public class LoginController {

    private final SessionManager sm;
    private final LoginService loginService;

    @RequestMapping("/")
    public ModelAndView apimain(HttpServletRequest req) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/login");
        return mav;
    }
    
    @RequestMapping("/login")
    public ModelAndView loginController(Login info, HttpServletResponse req) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("login");
        return mav;
    }

    @RequestMapping("/login.do")
    @ResponseBody
    public Map<String,Object> doLogin(@RequestBody Login info, HttpServletResponse res) {
        Map<String,Object> resultMap = new HashMap<String,Object>();
        
        boolean loginResult = loginService.loginProcess(info,res);
        resultMap.put("result", loginResult);
        
        if (loginResult) {
            String redirectUri = String.format("/%s/order",info.getGuboon());
            resultMap.put("redirectUri", redirectUri);
        } else {
            resultMap.put("msg", "로그인 실패");
        }
        return resultMap;
    }

    @RequestMapping("/logout")
    public ModelAndView logout(HttpServletRequest req) {
        ModelAndView mav = new ModelAndView();
        sm.expire(req);
        mav.setViewName("redirect:/login");
        return mav;
    }
}
