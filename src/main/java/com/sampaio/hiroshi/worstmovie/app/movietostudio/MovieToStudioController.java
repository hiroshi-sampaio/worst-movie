package com.sampaio.hiroshi.worstmovie.app.movietostudio;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("/movie-to-studio")
@RequiredArgsConstructor
public class MovieToStudioController {

    private final MovieToStudioRepository repository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieToStudio> get() {

        log.trace("get all");
        return repository.findAll();
    }

    @GetMapping("/movie/{movieId}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieToStudio> getByMovieId(@PathVariable @NotNull Long movieId) {

        log.trace("get {}", movieId);
        return repository.findByMovieId(movieId);
    }

    @GetMapping("/studio/{studioId}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieToStudio> getByStudioId(@PathVariable @NotNull Long studioId) {

        log.trace("get {}", studioId);
        return repository.findByStudioId(studioId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieToStudio> post(@RequestBody @NotNull MovieToStudio movieToStudio) {

        log.trace("post {}", movieToStudio);
        return repository.save(movieToStudio);
    }

    @DeleteMapping("/movie/{movieId}/studio/{studioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable @NotNull Long movieId, @PathVariable @NotNull Long studioId) {

        log.trace("delete by movie {} and studio {}", movieId, studioId);
        return repository.deleteByMovieIdAndStudioId(movieId, studioId);
    }

}
