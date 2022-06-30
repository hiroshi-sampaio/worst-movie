package com.sampaio.hiroshi.worstmovie.app.movie;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    Movie toMovie(MoviePayload moviePayload);

    @Mapping(target = "studios", ignore = true)
    @Mapping(target = "producers", ignore = true)
    MoviePayload toMoviePayload(Movie movie);
}
