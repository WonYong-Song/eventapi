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
                if (so.getBnsTpCode().equals("1")) {
                    //매도
                    so.setOrdPrc(so.getSellPrc());
                    hcm.sendOrder(user, so,resultMap);
                } else if (so.getBnsTpCode().equals("2")) {
                    //매수
                    so.setOrdPrc(so.getBuyPrc());
                    hcm.sendOrder(user, so,resultMap);
                } else if (so.getBnsTpCode().equals("3")) {
                    //반복
                    long sumPrc = so.getReadyBuyPrc();
                    if (so.getBuyPrc()==0 || so.getSellPrc()==0 || so.getOrdQty()==0) {
                        resultMap.put("msg", "가격,주문 수량 등을 확인해주세요");
                        return resultMap;
                    }
                    while(sumPrc<500000000L) {
                        try {
                            //매수
                            so.setOrdPrc(so.getBuyPrc());
                            so.setBnsTpCode("2");
                            hcm.sendOrder(user, so,resultMap);
                            Thread.sleep(500);
                            sumPrc = sumPrc+so.getBuyPrc()*so.getOrdQty();
                            System.out.println("sumPrc : " + sumPrc);
                            //매도
                            so.setOrdPrc(so.getSellPrc());
                            so.setBnsTpCode("1");
                            hcm.sendOrder(user, so,resultMap);
                            Thread.sleep(500);
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

    // @RequestMapping("/repeatSendOrder.do")
    // public ModelAndView requestMethodName(HttpServletRequest req) throws Exception{
    //     ModelAndView mav = new ModelAndView();
    //     Map<String,Object> resultMap = new HashMap<String,Object>();
                
    //     SendOrder so = new SendOrder();
    //     so.setIsuNo("459580");
    //     so.setOrdQty(39);
    //     boolean stopFlag = false;
    //     long buySum = 1071773070;
    //     Object obj = sm.getSession(req);
    //     if (obj instanceof Login) {
    //         Login user = (Login) obj;
    //         if (!user.getToken().equals("")) {
    //             while (!stopFlag) {
    //                 //매수
    //                 so.setOrdPrc(1007645);
    //                 so.setBnsTpCode("2");
    //                 hcm.sendOrder(user, so,resultMap);
    //                 buySum += 1007640*39;
    //                 Thread.sleep(500);
    //                 //매도
    //                 so.setOrdPrc(1007640);
    //                 so.setBnsTpCode("1");
    //                 hcm.sendOrder(user, so,resultMap);
    //                 Thread.sleep(500);

    //                 System.out.println(buySum); 
    //                 if (buySum > 4962271111L) {
    //                     stopFlag = true;
    //                     System.out.println("stop!");
    //                 }
    //             }
                
    //         } else {
    //             resultMap.put("msg", "token정보가 보이지 않습니다.\n재로그인해주세요");
    //         }
    //     } else {
    //         resultMap.put("msg", "token정보가 보이지 않습니다.\n재로그인해주세요");
    //     }
        

    //     mav.setViewName("order");
    //     return mav;
    // }
    
}
