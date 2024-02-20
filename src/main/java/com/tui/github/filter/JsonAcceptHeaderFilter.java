package com.tui.github.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.micrometer.common.util.StringUtils;



@Component
public class JsonAcceptHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                configureApplicationJsonHeader(request, response);
                configureAuthorizationToken(request, response);
                filterChain.doFilter(request, response);
    }

    private void configureApplicationJsonHeader(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String acceptHeader = request.getHeader("Accept");

        if (!(acceptHeader != null && (acceptHeader.contains("Application/Json") || acceptHeader.contains("application/json")))) {
            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Invalid Accept header");
        } 
    }

    private void configureAuthorizationToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if ( StringUtils.isEmpty(authorizationHeader) ) {
            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Authourization Bearer Token Must be present");
        } 
    }
}
