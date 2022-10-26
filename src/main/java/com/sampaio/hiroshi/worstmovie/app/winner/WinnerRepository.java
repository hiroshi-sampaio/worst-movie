package com.sampaio.hiroshi.worstmovie.app.winner;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface WinnerRepository extends R2dbcRepository<Winner, Integer> {
    Mono<Winner> findByPrizeYear(Integer prizeYear);

    Mono<Void> deleteByPrizeYear(Integer prizeYear);
}
