package com.swy.stockapi.order.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.swy.stockapi.login.dto.Login;
import com.swy.stockapi.order.dto.SendOrder;
import com.swy.stockapi.util.EbestHttpClientManager;
import com.swy.stockapi.util.HantooHttpClientManager;
import com.swy.stockapi.util.SessionManager;

import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final SessionManager sm;
    private final EbestHttpClientManager ehcm;
    private final HantooHttpClientManager hhcm;

    @RequestMapping("/{guboon}/order")
    public ModelAndView orderView(@PathVariable("guboon")String guboon, HttpServletRequest req) {
        
        Login user = (Login) sm.getSession(req);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("order");
        mav.addObject("guboon", user.getGuboon());
        
        return mav;
    }

    @RequestMapping("/{guboon}/sendOrder.do")
    @ResponseBody
    public Map<String,Object> sendOrder(@PathVariable("guboon")String guboon,@RequestBody SendOrder so, HttpServletRequest req) {
        Map<String,Object> resultMap = new HashMap<String,Object>();
        log.debug(so.toString());
        
        resultMap.put("result", false);

        Login user = (Login) sm.getSession(req);
        
        if (so.getBnsTpCode().equals("1")) {
            //매도
            so.setOrdPrc(so.getSellPrc());
            if (user.getGuboon().equals("ebest")) {
                ehcm.sendOrder(user, so,resultMap);
            } else if (user.getGuboon().equals("hantoo")) {
                hhcm.sendOrder(user, so,resultMap);
            }
        } else if (so.getBnsTpCode().equals("2")) {
            //매수
            so.setOrdPrc(so.getBuyPrc());
            if (user.getGuboon().equals("ebest")) {
                ehcm.sendOrder(user, so,resultMap);
            } else if (user.getGuboon().equals("hantoo")) {
                hhcm.sendOrder(user, so,resultMap);
            }
        } else if (so.getBnsTpCode().equals("3")) {
            //반복
            long sumPrc = 0L;
            if (so.getBuyPrc()==0 || so.getSellPrc()==0 || so.getOrdQty()==0) {
                resultMap.put("msg", "가격,주문 수량 등을 확인해주세요");
                return resultMap;
            }
            while(sumPrc<so.getTargetBuyPrc()) {
                try {
                    //매수
                    so.setOrdPrc(so.getBuyPrc());
                    so.setBnsTpCode("2");
                    if (user.getGuboon().equals("ebest")) {
                        ehcm.sendOrder(user, so,resultMap);
                    } else if (user.getGuboon().equals("hantoo")) {
                        hhcm.sendOrder(user, so,resultMap);
                    }
                    sumPrc = sumPrc+so.getBuyPrc()*so.getOrdQty();
                    log.info("sumPrc : " + sumPrc);
                    Thread.sleep(500);

                    //매도
                    so.setOrdPrc(so.getSellPrc());
                    so.setBnsTpCode("1");
                    if (user.getGuboon().equals("ebest")) {
                        ehcm.sendOrder(user, so,resultMap);
                    } else if (user.getGuboon().equals("hantoo")) {
                        hhcm.sendOrder(user, so,resultMap);
                    }
                    Thread.sleep(500);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                
            }
            resultMap.put("msg", "반복주문이 처리가 완료되었습니다.");
        }
        return resultMap;
    }
}