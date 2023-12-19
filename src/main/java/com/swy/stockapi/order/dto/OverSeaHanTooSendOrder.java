package com.swy.stockapi.order.dto;

import groovy.transform.ToString;
import lombok.Data;

@Data
@ToString
public class OverSeaHanTooSendOrder {
    //종합계좌번호 앞8자리
    String cano = "";
    //계좌상품코드 뒤2자리
    String acntPrdtCd = "";
    //해외거래소 코드
    String oversExcgCd = "";
    //종목번호 6자리
    String pdno = "";
    //주문수량
    String ordQty = "0";
    
    //주문구분 : 00 지정가
    String ordDvsn = "00";
    //주문가격
    String ovrsOrdUnpr = "0";

    // 매수가격
    float buyPrc = 0L;
    // 매도가격
    float sellPrc = 0L;
    // 매수 목표가격
    float targetPrc = 0L;
    //매매구분 : 매수,매도
    String bnsTpCode = "";
    // 주문서버구분코드
    String ordSvrDvsnCd = "0";
}
