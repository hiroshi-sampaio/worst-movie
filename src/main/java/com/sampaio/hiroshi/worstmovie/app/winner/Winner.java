package com.sampaio.hiroshi.worstmovie.app.winner;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;

@Data
@Builder(toBuilder = true)
@Jacksonized
public class Winner {
    @Id
    private Integer prizeYear;
    private Long movieId;
}
