package com.sampaio.hiroshi.worstmovie.app.movie;

import com.sampaio.hiroshi.worstmovie.app.producer.Producer;
import com.sampaio.hiroshi.worstmovie.app.studio.Studio;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder(toBuilder = true)
@Jacksonized
public class MoviePayload {

    private Long id;
    private String title;
    private Integer releaseYear;
    private Iterable<Producer> producers;
    private Iterable<Studio> studios;
}
