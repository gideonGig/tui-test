package com.tui.github.filter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.tui.github.exception.GitResponseStatusException;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;



@Component
@Slf4j
public class AcceptHeaderFilter implements WebFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange,  WebFilterChain webFilterChain) {
        String acceptHeader = exchange.getRequest().getHeaders().getFirst("Accept");

        if (!(acceptHeader != null && (acceptHeader.toLowerCase().contains("application/json")))) {
            log.error("Invalid accept header was provided");
            throw new GitResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid Accept header field");
        }

        String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (StringUtils.isEmpty(authorizationHeader)) {
          log.error("Invalid Authorization header was provided");
           throw new GitResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Authorization bearer token must be present");
        }

        return webFilterChain.filter(exchange);
    }

}
