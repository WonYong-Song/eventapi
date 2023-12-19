package com.swy.stockapi.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.swy.stockapi.login.dto.Login;
import com.swy.stockapi.util.SessionManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserCheckFilter extends OncePerRequestFilter{

    private SessionManager sm;

    public UserCheckFilter () {
        sm = new SessionManager();
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        log.info("usercheckfilter!");
        String uri = request.getRequestURI();
        Object obj = sm.getSession(request);
        // PathVariable 값 가져오기
        String guboon = request.getAttribute("guboon") != null ? request.getAttribute("guboon").toString() : "";

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
