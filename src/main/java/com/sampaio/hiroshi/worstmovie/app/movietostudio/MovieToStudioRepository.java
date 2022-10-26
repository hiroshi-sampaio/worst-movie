package com.sampaio.hiroshi.worstmovie.app.movietostudio;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieToStudioRepository extends R2dbcRepository<MovieToStudio, Void> {
    Flux<MovieToStudio> findByMovieId(Long movieId);

    Flux<MovieToStudio> findByStudioId(Long studioId);

    Flux<Void> deleteByMovieId(Long movieId);

    Mono<Void> deleteByMovieIdAndStudioId(Long movieId, Long studioId);
}
