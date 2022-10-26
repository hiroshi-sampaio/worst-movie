package com.sampaio.hiroshi.worstmovie.app.movietoproducer;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieToProducerRepository extends R2dbcRepository<MovieToProducer, Void> {
    Flux<MovieToProducer> findByMovieId(Long movieId);

    Flux<MovieToProducer> findByProducerId(Long movieId);

    Flux<Void> deleteByMovieId(Long movieId);

    Mono<Void> deleteByMovieIdAndProducerId(Long movieId, Long producerId);
}
