package com.sampaio.hiroshi.worstmovie.app.studio;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface StudioRepository extends R2dbcRepository<Studio, Long> {
}
