package com.sampaio.hiroshi.worstmovie.app.logging;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;

import java.io.ByteArrayOutputStream;
import java.nio.channels.Channels;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class DataBufferResponseConsumer implements Consumer<DataBuffer> {

    private final ServerHttpResponse delegate;

    @Override
    @SneakyThrows
    public void accept(final DataBuffer dataBuffer) {
        var headers = delegate.getHeaders().toString();

        var bodyStream = new ByteArrayOutputStream();

        Channels.newChannel(bodyStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());

        log.debug("Response:\n{}\n\n{}\n", headers, bodyStream);
    }
}
