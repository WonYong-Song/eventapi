package com.swy.stockapi.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import com.swy.stockapi.login.dto.Login;
import com.swy.stockapi.order.dto.OverSeaHanTooSendOrder;
import com.swy.stockapi.order.dto.SendOrder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HantooHttpClientManager {
    
    String httpDomain = "https://openapi.koreainvestment.com:9443";

    public boolean tryLogin(Login info) {
        log.info("try login to Hantoo");
        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("result", false);

        String loginUrl = "/oauth2/tokenP";
        List<NameValuePair> headers = new ArrayList<>();

        JSONObject jo = new JSONObject();
        jo.put("grant_type", info.getGrant_type());
        jo.put("appkey", info.getAppKey());
        jo.put("appsecret", info.getSecretKey());
        Map<String,Object> connectionMap = httpConnectLogin2(loginUrl, headers, jo);
        if ((int)connectionMap.get("code") != 200) {
            return false;
        }
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse((String)connectionMap.get("msg"));
            info.setToken((String)jsonObject.get("access_token"));
            returnMap.put("result", true);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void sendOrder(Login info, SendOrder so, Map<String,Object> returnMap) {

        String sendOrderUrl = "/uapi/domestic-stock/v1/trading/order-cash";
        List<NameValuePair> headers = new ArrayList<>();
        headers.add(new BasicNameValuePair("Content-Type", "application/json; charset=utf-8"));
        headers.add(new BasicNameValuePair("authorization", "Bearer "+info.getToken()));
        headers.add(new BasicNameValuePair("appkey", info.getAppKey()));
        headers.add(new BasicNameValuePair("appsecret", info.getSecretKey()));
        if (so.getBnsTpCode().equals("2")) {
            //매수
            headers.add(new BasicNameValuePair("tr_id", "TTTC0802U"));
        } else if (so.getBnsTpCode().equals("1")) {
            //매도
            headers.add(new BasicNameValuePair("tr_id", "TTTC0801U"));
        }
        
        JSONObject jo = new JSONObject();
        jo.put("CANO", so.getCano());
        jo.put("ACNT_PRDT_CD", so.getAcntPrdtCd());
        jo.put("PDNO", so.getIsuNo());
        jo.put("ORD_DVSN", so.getOrdprcPtnCode());
        jo.put("ORD_QTY", String.valueOf(so.getOrdQty()));
        jo.put("ORD_UNPR", String.valueOf(so.getOrdPrc()));

        Map<String,Object> connectionMap = httpConnectOrder(sendOrderUrl, headers, jo);
        if ((int)connectionMap.get("code") != 200) {
            returnMap.put("msg", "통신 중 에러발생 응답코드 : " + (int)connectionMap.get("code"));
        } else {
            try {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject)jsonParser.parse((String)connectionMap.get("msg"));
                String rsp_cd = (String) jsonObject.get("msg1");
                returnMap.put("msg", rsp_cd);
            } catch (Exception e) {
                returnMap.put("msg", "파싱에러");
            }
        }
    }

    public void sendOverSeaOrder(Login info, OverSeaHanTooSendOrder so, Map<String,Object> returnMap) {

        String sendOrderUrl = "/uapi/overseas-stock/v1/trading/order";
        
        List<NameValuePair> headers = new ArrayList<>();
        headers.add(new BasicNameValuePair("Content-Type", "application/json; charset=utf-8"));
        headers.add(new BasicNameValuePair("authorization", "Bearer "+info.getToken()));
        headers.add(new BasicNameValuePair("appkey", info.getAppKey()));
        headers.add(new BasicNameValuePair("appsecret", info.getSecretKey()));

        if (so.getBnsTpCode().equals("2")) {
            //매수
            if (so.getOversExcgCd().equals("NASD") || so.getOversExcgCd().equals("NYSE") || so.getOversExcgCd().equals("AMEX")) {
                headers.add(new BasicNameValuePair("tr_id", "TTTT1002U"));
            } else if (so.getOversExcgCd().equals("SEHK")) {
                headers.add(new BasicNameValuePair("tr_id", "TTTS1002U"));
            } else if (so.getOversExcgCd().equals("SHAA")) {
                headers.add(new BasicNameValuePair("tr_id", "TTTS0202U"));
            } else if (so.getOversExcgCd().equals("SZAA")) {
                headers.add(new BasicNameValuePair("tr_id", "TTTS0305U"));
            } else if (so.getOversExcgCd().equals("TKSE")) {
                headers.add(new BasicNameValuePair("tr_id", "TTTS0308U"));
            } else if (so.getOversExcgCd().equals("HASE") || so.getOversExcgCd().equals("VNSE")) {
                headers.add(new BasicNameValuePair("tr_id", "TTTS0311U"));
            }
            
        } else if (so.getBnsTpCode().equals("1")) {
            //매도
            if (so.getOversExcgCd().equals("NASD") || so.getOversExcgCd().equals("AMEX")) {
                headers.add(new BasicNameValuePair("tr_id", "TTTT1006U"));
            } else if (so.getOversExcgCd().equals("SEHK")) {
                headers.add(new BasicNameValuePair("tr_id", "TTTS1001U"));
            } else if (so.getOversExcgCd().equals("SHAA")) {
                headers.add(new BasicNameValuePair("tr_id", "TTTS1005U"));
            } else if (so.getOversExcgCd().equals("SZAA")) {
                headers.add(new BasicNameValuePair("tr_id", "TTTS0304U"));
            } else if (so.getOversExcgCd().equals("TKSE")) {
                headers.add(new BasicNameValuePair("tr_id", "TTTS0307U"));
            } else if (so.getOversExcgCd().equals("HASE") || so.getOversExcgCd().equals("VNSE")) {
                headers.add(new BasicNameValuePair("tr_id", "TTTS0310U"));
            }
        }
        
        JSONObject jo = new JSONObject();
        jo.put("CANO", so.getCano());
        jo.put("ACNT_PRDT_CD", so.getAcntPrdtCd());
        jo.put("OVRS_EXCG_CD", so.getOversExcgCd());
        jo.put("PDNO", so.getPdno());
        jo.put("ORD_QTY", so.getOrdQty());
        jo.put("OVRS_ORD_UNPR", so.getOvrsOrdUnpr());
        jo.put("ORD_SVR_DVSN_CD", so.getOrdSvrDvsnCd());
        jo.put("ORD_DVSN", so.getOrdDvsn());

        Map<String,Object> connectionMap = httpConnectOrder(sendOrderUrl, headers, jo);
        if ((int)connectionMap.get("code") != 200) {
            returnMap.put("msg", "통신 중 에러발생 응답코드 : " + (int)connectionMap.get("code"));
        } else {
            try {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject)jsonParser.parse((String)connectionMap.get("msg"));
                String rsp_cd = (String) jsonObject.get("msg1");
                returnMap.put("msg", rsp_cd);
            } catch (Exception e) {
                returnMap.put("msg", "파싱에러");
            }
        }
    }
    
    public Map<String,Object> httpConnectLogin(String url, List<NameValuePair> headers, List<NameValuePair> params) {
        Map<String,Object> returnMap = new HashMap<>();

        String finalUrl = httpDomain+url;
        CloseableHttpClient httpClient = null;
        try {
            // URI를 생성합니다.
            URIBuilder uriBuilder = new URIBuilder(finalUrl);

            // HttpClient 객체 생성
            httpClient = HttpClients.createDefault();

            // HttpPost 객체 생성 및 파라미터 설정
            HttpPost httpPost = new HttpPost(uriBuilder.build());
            for (NameValuePair param : params) {
                log.debug("Hantoo Params" + param.getName() + " : " + param.getValue());
            }
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            // HTTP 요청을 보내고 응답을 받습니다.
            HttpResponse response = httpClient.execute(httpPost);

            // 응답의 상태 코드 확인 (200이면 성공)
            int statusCode = response.getStatusLine().getStatusCode();
            log.info("Status Code: " + statusCode);
            returnMap.put("code", statusCode);

            // 응답의 내용을 문자열로 변환하여 출력합니다.
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);
            log.info("Response: " + responseString);
            returnMap.put("msg", responseString);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        } finally {
            if (httpClient !=null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnMap;
    }

    public Map<String,Object> httpConnectOrder(String url, List<NameValuePair> headers, JSONObject params) {
        Map<String,Object> returnMap = new HashMap<>();

        String finalUrl = httpDomain+url;
        CloseableHttpClient httpClient = null;
        try {
            // URI를 생성합니다.
            URIBuilder uriBuilder = new URIBuilder(finalUrl);

            // HttpClient 객체 생성
            httpClient = HttpClients.createDefault();

            // HttpPost 객체 생성 및 파라미터 설정
            HttpPost httpPost = new HttpPost(uriBuilder.build());
            
            StringEntity stringEntity = new StringEntity(params.toJSONString(), ContentType.APPLICATION_JSON);
            log.debug(params.toJSONString());
            httpPost.setEntity(stringEntity);

            // Header에 content-type 설정
            for (NameValuePair header : headers) {
                log.debug("header name : " + header.getName() + ", value : " + header.getValue());
                httpPost.setHeader(header.getName(), header.getValue());
            }

            // HTTP 요청을 보내고 응답을 받습니다.
            HttpResponse response = httpClient.execute(httpPost);

            // 응답의 상태 코드 확인 (200이면 성공)
            int statusCode = response.getStatusLine().getStatusCode();
            log.debug("Status Code: " + statusCode);
            returnMap.put("code", statusCode);

            // 응답의 내용을 문자열로 변환하여 출력합니다.
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);
            log.info("Response: " + responseString);
            returnMap.put("msg", responseString);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        } finally {
            if (httpClient !=null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnMap;
    }

    public Map<String,Object> httpConnectLogin2(String url, List<NameValuePair> headers, JSONObject params) {
        Map<String,Object> returnMap = new HashMap<>();

        String finalUrl = httpDomain+url;
        CloseableHttpClient httpClient = null;
        try {
            // URI를 생성합니다.
            URIBuilder uriBuilder = new URIBuilder(finalUrl);

            // HttpClient 객체 생성
            httpClient = HttpClients.createDefault();

            // HttpPost 객체 생성 및 파라미터 설정
            HttpPost httpPost = new HttpPost(uriBuilder.build());
            
            StringEntity stringEntity = new StringEntity(params.toJSONString(), ContentType.APPLICATION_JSON);
            log.debug(params.toJSONString());
            httpPost.setEntity(stringEntity);

            // Header에 content-type 설정
            // for (NameValuePair header : headers) {
            //     System.out.println(header.getName() + " : " + header.getValue());
            //     httpPost.setHeader(header.getName(), header.getValue());
            // }

            // HTTP 요청을 보내고 응답을 받습니다.
            HttpResponse response = httpClient.execute(httpPost);

            // 응답의 상태 코드 확인 (200이면 성공)
            int statusCode = response.getStatusLine().getStatusCode();
            log.debug("Status Code: " + statusCode);
            returnMap.put("code", statusCode);

            // 응답의 내용을 문자열로 변환하여 출력합니다.
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);
            log.debug("Response: " + responseString);
            returnMap.put("msg", responseString);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        } finally {
            if (httpClient !=null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return returnMap;
    }
}
