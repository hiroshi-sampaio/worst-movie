package com.sampaio.hiroshi.worstmovie.app.winner;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface WinnerRepository extends R2dbcRepository<Winner, Integer> {
}
