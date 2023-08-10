package com.swy.ebestapi.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.swy.ebestapi.login.dto.Login;
import com.swy.ebestapi.login.service.LoginService;
import com.swy.ebestapi.util.SessionManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final SessionManager sm;
    private final LoginService loginService;

    @RequestMapping("/")
    public ModelAndView apimain(HttpServletRequest req) {
        ModelAndView mav = new ModelAndView();
        Object obj = sm.getSession(req);
        if (obj instanceof Login) {
            Login user = (Login) obj;
            if (!user.getToken().equals("")) {
                mav.setViewName("redirect:/order");
            } else {
                sm.expire(req);
                mav.setViewName("redirect:/login");
            }
        } else {
            mav.setViewName("redirect:/login");
        }
        return mav;
    }

    // @RequestMapping("/login")
    // public ModelAndView login(ModelAndView mav) {
        
    //     mav.setViewName("login");
    //     return mav;
    // }
    
    @RequestMapping("/login")
    public ModelAndView loginController(Login info, HttpServletResponse req) {
        ModelAndView mav = new ModelAndView();
        
        System.out.println("login : " + info.toString());
        /**
         *로그인 로직 처리
         */
        mav.addObject("info", info);
        if (info.getAppKey().equals("") && info.getSecretKey().equals("")) {
            mav.setViewName("login");
            return mav;
        }
        boolean loginResult = loginService.loginProcess(info,req);
        if (loginResult) {
            mav.setViewName("redirect:/order");
        } else {
            mav.addObject("msg", "로그인 실패");
            mav.setViewName("login");
        }
        return mav;
    }

    @RequestMapping("/logout")
    public ModelAndView logout(HttpServletRequest req) {
        ModelAndView mav = new ModelAndView();
        sm.expire(req);
        mav.setViewName("redirect:/");
        return mav;
    }
}
