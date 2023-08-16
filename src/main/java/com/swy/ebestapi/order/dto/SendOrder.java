package com.swy.ebestapi.order.dto;

import groovy.transform.ToString;
import lombok.Data;

@Data
@ToString
public class SendOrder {
    //종목번호
    String isuNo = "";
    //주문수량
    int ordQty = 0;
    // 기매수가격
    long readyBuyPrc = 0L;
    //주문가격
    int ordPrc = 0;
    // 매수가격
    int buyPrc = 0;
    // 매도가격
    int sellPrc =0;
    //매매구분 : 매수,매도
    String bnsTpCode = "";
    //호가유형코드 : 00 지정가
    String ordprcPtnCode = "00";
    //신용거래코드 : 000 보통
    String mgntrnCode = "000";
    //대출일
    String loanDt = "";
    //주문조건구분 : 	0:없음,1:IOC,2:FOK
    String ordCndiTpCode = "0";
}
