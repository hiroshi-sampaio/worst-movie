package com.sampaio.hiroshi.worstmovie.app.winner;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder(toBuilder = true)
@Jacksonized
public class Winner {
    private Integer prizeYear;
    private Long movieId;
}
