package com.sampaio.hiroshi.worstmovie.app.movietoproducer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class MovieToProducer {

    private Long movieId, producerId;
}
