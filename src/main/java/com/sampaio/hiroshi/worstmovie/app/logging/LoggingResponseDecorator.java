package com.sampaio.hiroshi.worstmovie.app.logging;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Mono;

@Slf4j
public class LoggingResponseDecorator extends ServerHttpResponseDecorator {
    public LoggingResponseDecorator(final ServerHttpResponse delegate) {
        super(delegate);
    }

    @Override
    public Mono<Void> writeWith(final Publisher<? extends DataBuffer> body) {
        if (log.isDebugEnabled() || log.isTraceEnabled()) {
            return super.writeWith(
                    Mono.from(body).doOnNext(new DataBufferResponseConsumer(getDelegate())));
        } else {
            return super.writeWith(body);
        }
    }
}
