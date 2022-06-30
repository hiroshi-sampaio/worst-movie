package com.sampaio.hiroshi.worstmovie.app.movie;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder(toBuilder = true)
public class Movie {

    @Id
    private Long id;
    private String title;
    private Integer releaseYear;
}
