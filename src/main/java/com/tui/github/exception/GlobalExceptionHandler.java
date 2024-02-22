package com.tui.github.exception;

import java.util.Map;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.tui.github.model.ErrorResponse;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {
    private final Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode;
    private final HttpStatus defaultStatus;

    public GlobalExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources,
    ApplicationContext applicationContext, Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode, ServerCodecConfigurer configurer) {
                super(errorAttributes, resources, applicationContext);
                this.exceptionToStatusCode = exceptionToStatusCode;
                this.defaultStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                this.setMessageWriters(configurer.getWriters());

    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    @SuppressWarnings("null")
    private Mono<ServerResponse> renderErrorResponse(org.springframework.web.reactive.function.server.ServerRequest request) {

        Throwable error = getError(request);
        log.error("An error has been occurred", error);
        HttpStatus httpStatus;
        if (error instanceof GitResponseStatusException exception) {
            httpStatus = exceptionToStatusCode.getOrDefault(exception.getClass(), exception.getStatus());
        } else {
            httpStatus = defaultStatus;
        }
        return ServerResponse
                .status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ErrorResponse
                        .builder()
                        .status(httpStatus.value())
                        .message(error.getMessage())
                        .build()
                );
    }
}
