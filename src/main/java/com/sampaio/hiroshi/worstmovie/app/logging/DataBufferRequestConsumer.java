package com.sampaio.hiroshi.worstmovie.app.logging;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.io.ByteArrayOutputStream;
import java.nio.channels.Channels;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
public class DataBufferRequestConsumer implements Consumer<DataBuffer> {

    private final ServerHttpRequest delegate;

    @Override
    @SneakyThrows
    public void accept(final DataBuffer dataBuffer) {
        var path = delegate.getURI().getPath();
        var query = delegate.getURI().getQuery();
        var method = Optional.ofNullable(delegate.getMethod()).orElse(HttpMethod.GET).name();
        var headers = delegate.getHeaders().toString();

        var bodyStream = new ByteArrayOutputStream();

        Channels.newChannel(bodyStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());

        log.debug("Request:\n{} {}\n{}\n\n{}\n",
                method,
                path + (nonNull(query) ? query : ""),
                headers,
                bodyStream);
    }
}
