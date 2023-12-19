package com.swy.stockapi.config;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.swy.stockapi.filter.UriCheckFilter;
import com.swy.stockapi.filter.UserCheckFilter;

import lombok.RequiredArgsConstructor;

@Configuration
public class WebConfig implements WebMvcConfigurer{

    @Bean
    public FilterRegistrationBean<Filter> logFilter() {
        
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<Filter>();
        filterRegistrationBean.setFilter(new UriCheckFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/*");

        return filterRegistrationBean;
    }
    @Bean
    public FilterRegistrationBean<Filter> sessionCheckFilter() {
        
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<Filter>();
        filterRegistrationBean.setFilter(new UserCheckFilter());
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.addUrlPatterns("/login","/ebest/order","/ebest/sendOrder.do","/hantoo/order","/hantoo/sendOrder.do");

        return filterRegistrationBean;
    }
}
