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
import com.swy.stockapi.order.dto.SendOrder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EbestHttpClientManager {
    String httpDomain = "https://openapi.ebestsec.co.kr:8080";

    public boolean tryLogin(Login info) {
        log.info("try login to ebest");
        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("result", false);

        String loginUrl = "/oauth2/token";
        List<NameValuePair> headers = new ArrayList<>();
        headers.add(new BasicNameValuePair("Content-Type", "application/x-www-form-urlencoded"));
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", info.getGrant_type()));
        params.add(new BasicNameValuePair("appkey", info.getAppKey()));
        params.add(new BasicNameValuePair("appsecretkey", info.getSecretKey()));
        params.add(new BasicNameValuePair("scope", info.getScope()));
        // StringBuilder sb = new StringBuilder();
        // sb.append("{\"grant_type\":\""+info.getGrant_type()+"\",");
        // sb.append("{\"appkey\":\""+info.getAppKey()+"\",");
        // sb.append("{\"appsecretkey\":\""+info.getSecretKey()+"\",");
        // sb.append("{\"scope\":\""+info.getScope()+"\"}");
        Map<String,Object> connectionMap = httpConnectLogin(loginUrl, headers, params);
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

    public void sendOrder(Login info,SendOrder so,Map<String,Object> returnMap) {

        String sendOrderUrl = "/stock/order";
        List<NameValuePair> headers = new ArrayList<>();
        headers.add(new BasicNameValuePair("Content-Type", "application/json; charset=utf-8"));
        headers.add(new BasicNameValuePair("authorization", "Bearer "+info.getToken()));
        headers.add(new BasicNameValuePair("tr_cd", "CSPAT00601"));
        headers.add(new BasicNameValuePair("tr_cont", "N"));
        headers.add(new BasicNameValuePair("tr_cont_key", ""));
        headers.add(new BasicNameValuePair("mac_address", ""));
        
        JSONObject params = new JSONObject();
        
        JSONObject jo = new JSONObject();
        jo.put("IsuNo", so.getIsuNo());
        jo.put("OrdQty", so.getOrdQty());
        jo.put("OrdPrc", so.getOrdPrc());
        jo.put("BnsTpCode", so.getBnsTpCode());
        jo.put("OrdprcPtnCode", so.getOrdprcPtnCode());
        jo.put("MgntrnCode", so.getMgntrnCode());
        jo.put("LoanDt", so.getLoanDt());
        jo.put("OrdCndiTpCode", so.getOrdCndiTpCode());
        
        params.put("CSPAT00601InBlock1", jo);

        Map<String,Object> connectionMap = httpConnectOrder(sendOrderUrl, headers, params);
        if ((int)connectionMap.get("code") != 200) {
            returnMap.put("msg", "통신 중 에러발생 응답코드 : " + (int)connectionMap.get("code"));
        } else {
            try {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject)jsonParser.parse((String)connectionMap.get("msg"));
                String rsp_cd = (String) jsonObject.get("rsp_msg");
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
                log.debug("ebest Param : " + param.getName() + " : " + param.getValue());
            }
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            // Header에 content-type 설정
            for (NameValuePair header : headers) {
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
            // StringEntity stringEntity = new StringEntity(params, ContentType.APPLICATION_JSON);
            StringEntity stringEntity = new StringEntity(params.toJSONString(), ContentType.APPLICATION_JSON);
            log.debug(params.toJSONString());
            httpPost.setEntity(stringEntity);

            // Header에 content-type 설정
            for (NameValuePair header : headers) {
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
}
