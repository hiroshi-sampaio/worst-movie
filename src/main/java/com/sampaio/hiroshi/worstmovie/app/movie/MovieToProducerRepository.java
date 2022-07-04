package com.sampaio.hiroshi.worstmovie.app.movie;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface MovieToProducerRepository extends R2dbcRepository<MovieToProducer, Void> {

    Mono<Void> deleteByMovieId(Long movieId);

    Mono<Void> deleteByMovieIdAndProducerId(Long movieId, Long producerId);
}
