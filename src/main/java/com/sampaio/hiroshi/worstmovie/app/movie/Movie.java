package com.sampaio.hiroshi.worstmovie.app.movie;

import com.sampaio.hiroshi.worstmovie.app.producer.Producer;
import com.sampaio.hiroshi.worstmovie.app.studio.Studio;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;

@Data
@Builder
@Jacksonized
public class Movie {

    @Id
    private Long id;
    private String title;
    private Integer year;
    private Iterable<Producer> producers;
    private Iterable<Studio> studios;
}
