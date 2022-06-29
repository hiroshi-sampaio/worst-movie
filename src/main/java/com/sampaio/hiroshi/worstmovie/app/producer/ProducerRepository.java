package com.sampaio.hiroshi.worstmovie.app.producer;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProducerRepository extends ReactiveCrudRepository<Producer, Long> {
}
