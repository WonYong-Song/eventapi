package com.swy.stockapi.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import com.swy.stockapi.login.dto.Login;
import com.swy.stockapi.util.SessionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UserCheckFilter extends OncePerRequestFilter{

    private final SessionManager sm;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        log.info("usercheckfilter!");
        String uri = request.getRequestURI();
        Object obj = sm.getSession(request);
        // PathVariable 값 가져오기
        // String guboon = request.getParameter("guboon") != null ? request.getParameter("guboon") : "";

        // URI_TEMPLATE_VARIABLES_ATTRIBUTE 속성을 사용하여 PathVariable 값을 가져옴
        Map<String, String> uriVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        // 원하는 PathVariable 이름을 사용하여 값을 가져옴
        String guboon = "";
        if (uriVariables != null) {
            uriVariables.get("guboon");
        }
        
        if (obj != null || obj instanceof Login) {
            Login user = (Login) obj;
            if (uri.equals("/login") || !user.getGuboon().equals(guboon)) {
                response.sendRedirect(String.format("/%s/order",user.getGuboon()));
                return;
            }
        } else if (!uri.equals("/login")) {
            log.info("세션이 없네~");
            response.sendRedirect("/login");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
