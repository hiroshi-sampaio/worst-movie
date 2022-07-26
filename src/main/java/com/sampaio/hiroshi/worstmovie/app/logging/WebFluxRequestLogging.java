package com.sampaio.hiroshi.worstmovie.app.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Slf4j
@Component
public class WebFluxRequestLogging implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        var request = exchange.getRequest();
        log.trace("{} {} {}",
                request.getMethod(),
                request.getURI(),
                request.getHeaders()
                        .toSingleValueMap()
                        .entrySet()
                        .stream()
                        .map(e -> "\n" + e.getKey() + " = " + e.getValue())
                        .collect(Collectors.joining()));
        return chain.filter(new LoggingWebExchange(exchange));
    }
}
