package com.sampaio.hiroshi.worstmovie.app.movie;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class MovieToProducer {

    private Long movieId, producerId;
}
