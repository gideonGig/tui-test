package com.tui.github.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonHeaderFilterConfig {

    @Bean
    public FilterRegistrationBean<JsonAcceptHeaderFilter> jsonAcceptHeaderFilterConfig() {
        FilterRegistrationBean<JsonAcceptHeaderFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JsonAcceptHeaderFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(3); 
        return registrationBean;
    }
}
