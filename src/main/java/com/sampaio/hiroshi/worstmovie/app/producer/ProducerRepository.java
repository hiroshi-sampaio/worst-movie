package com.sampaio.hiroshi.worstmovie.app.producer;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ProducerRepository extends R2dbcRepository<Producer, Long> {


}
