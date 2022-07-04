package com.sampaio.hiroshi.worstmovie.app.movie;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface MovieToStudioRepository extends R2dbcRepository<MovieToStudio, Void> {

    Mono<Void> deleteByMovieId(Long movieId);

    Mono<Void> deleteByMovieIdAndStudioId(Long movieId, Long studioId);
}
