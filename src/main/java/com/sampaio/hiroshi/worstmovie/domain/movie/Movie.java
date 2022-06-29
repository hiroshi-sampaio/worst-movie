package com.sampaio.hiroshi.worstmovie.domain.movie;

import com.sampaio.hiroshi.worstmovie.domain.producer.Producer;
import com.sampaio.hiroshi.worstmovie.domain.studio.Studio;
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
