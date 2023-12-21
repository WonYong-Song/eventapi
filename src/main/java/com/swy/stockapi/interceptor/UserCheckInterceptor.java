package com.swy.stockapi.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import com.swy.stockapi.login.dto.Login;
import com.swy.stockapi.util.SessionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UserCheckInterceptor implements HandlerInterceptor {

    private final SessionManager sm;
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable Exception ex) throws Exception {
        
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable ModelAndView modelAndView) throws Exception {
        
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        log.info("usercheck interceptor prehandle!");
        String uri = request.getRequestURI();
        Object obj = sm.getSession(request);
        // PathVariable 값 가져오기
        // String guboon = request.getAttribute("guboon") != null ? request.getAttribute("guboon").toString() : "";
        
        // URI_TEMPLATE_VARIABLES_ATTRIBUTE 속성을 사용하여 PathVariable 값을 가져옴
        Map<String, String> uriVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        // 원하는 PathVariable 이름을 사용하여 값을 가져옴
        String guboon = uriVariables.get("guboon");
        log.info("guboon : " + guboon);

        if (obj != null || obj instanceof Login) {
            Login user = (Login) obj;
            if (uri.equals("/login") || !user.getGuboon().equals(guboon)) {
                response.sendRedirect(String.format("/%s/order",user.getGuboon()));
                return false;
            }
        } else if (!uri.equals("/login")) {
            log.info("세션이 없네~");
            response.sendRedirect("/login");
            return false;
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
    
    
}
