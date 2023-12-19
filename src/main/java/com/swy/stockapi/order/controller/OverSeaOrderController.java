package com.swy.stockapi.order.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.swy.stockapi.login.dto.Login;
import com.swy.stockapi.order.dto.OverSeaHanTooSendOrder;
import com.swy.stockapi.util.HantooHttpClientManager;
import com.swy.stockapi.util.SessionManager;

import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
@RequestMapping("/oversea")
public class OverSeaOrderController {
    private final SessionManager sm;
    private final HantooHttpClientManager hcm;

    @RequestMapping("/order")
    public ModelAndView orderView() {
        ModelAndView mav = new ModelAndView();

        mav.setViewName("overseaOrder");
        return mav;
    }

    @RequestMapping("/sendOrder.do")
    @ResponseBody
    public Map<String,Object> overSeaSendOrder(@RequestBody OverSeaHanTooSendOrder so, HttpServletRequest req) {
        Map<String,Object> resultMap = new HashMap<String,Object>();
        System.out.println(so.toString());
        
        resultMap.put("result", false);

        Object obj = sm.getSession(req);
        if (obj instanceof Login) {
            Login user = (Login) obj;
            if (!user.getToken().equals("")) {
                if (so.getBnsTpCode().equals("1")) {
                    //매도
                    so.setOvrsOrdUnpr(Float.toString(so.getSellPrc()));
                    hcm.sendOverSeaOrder(user, so,resultMap);
                } else if (so.getBnsTpCode().equals("2")) {
                    //매수
                    so.setOvrsOrdUnpr(Float.toString(so.getBuyPrc()));
                    hcm.sendOverSeaOrder(user, so,resultMap);
                } else if (so.getBnsTpCode().equals("3")) {
                    //반복
                    float sumPrc = 0;
                    if (so.getTargetPrc()==0 || so.getBuyPrc()==0 || so.getSellPrc()==0 || (so.getOrdQty()==null || so.getOrdQty().equals("0"))) {
                        resultMap.put("msg", "가격,주문 수량 등을 확인해주세요");
                        return resultMap;
                    }
                    while(sumPrc<so.getTargetPrc()) {
                        try {
                            //매수
                            so.setOvrsOrdUnpr(Float.toString(so.getBuyPrc()));
                            so.setBnsTpCode("2");
                            hcm.sendOverSeaOrder(user, so,resultMap);
                            Thread.sleep(1000);
                            sumPrc = sumPrc + so.getBuyPrc() * Integer.parseInt(so.getOrdQty());
                            System.out.println("buy sumPrc : " + sumPrc);
                            //매도
                            so.setOvrsOrdUnpr(Float.toString(so.getSellPrc()));
                            so.setBnsTpCode("1");
                            hcm.sendOverSeaOrder(user, so,resultMap);
                            Thread.sleep(1000);
                            System.out.println("sell sumPrc : " + sumPrc);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        
                    }
                    resultMap.put("msg", "반복주문이 처리가 완료되었습니다.");
                }
                
            } else {
                resultMap.put("msg", "token정보가 보이지 않습니다.\n재로그인해주세요");
            }
        } else {
            resultMap.put("msg", "token정보가 보이지 않습니다.\n재로그인해주세요");
        }
        return resultMap;
    }

    @RequestMapping()
    public ModelAndView overseaOrderView() {
        ModelAndView mav = new ModelAndView();

        mav.setViewName("overseaOrder");
        return mav;
    }
}
