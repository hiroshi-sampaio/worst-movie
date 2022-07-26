package com.sampaio.hiroshi.worstmovie.app.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

@Slf4j
public class LoggingRequestDecorator extends ServerHttpRequestDecorator {

    private final Flux<DataBuffer> body;

    public LoggingRequestDecorator(final ServerHttpRequest delegate) {
        super(delegate);

        this.body = log.isDebugEnabled() || log.isTraceEnabled()
                ? super.getBody().doOnNext(new DataBufferRequestConsumer(getDelegate()))
                : super.getBody();
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return body;
    }
}
