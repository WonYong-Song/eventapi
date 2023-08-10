package com.swy.ebestapi.order.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.swy.ebestapi.login.dto.Login;
import com.swy.ebestapi.order.dto.SendOrder;
import com.swy.ebestapi.util.HttpClientManager;
import com.swy.ebestapi.util.SessionManager;

import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final SessionManager sm;
    private final HttpClientManager hcm;

    @RequestMapping("/order")
    public ModelAndView orderView() {
        ModelAndView mav = new ModelAndView();

        mav.setViewName("order");
        return mav;
    }

    @RequestMapping("/sendOrder.do")
    @ResponseBody
    public Map<String,Object> sendOrder(@RequestBody SendOrder so, HttpServletRequest req) {
        Map<String,Object> resultMap = new HashMap<String,Object>();
        System.out.println(so.toString());
        
        resultMap.put("result", false);

        Object obj = sm.getSession(req);
        if (obj instanceof Login) {
            Login user = (Login) obj;
            if (!user.getToken().equals("")) {
                hcm.sendOrder(user, so,resultMap);
            } else {
                resultMap.put("msg", "token정보가 보이지 않습니다.\n재로그인해주세요");
            }
        } else {
            resultMap.put("msg", "token정보가 보이지 않습니다.\n재로그인해주세요");
        }
        return resultMap;
    }
}
