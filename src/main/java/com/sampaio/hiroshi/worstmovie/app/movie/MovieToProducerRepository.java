package com.sampaio.hiroshi.worstmovie.app.movie;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface MovieToProducerRepository extends R2dbcRepository<MovieToProducer, Void> {
}
