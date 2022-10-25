package com.sampaio.hiroshi.worstmovie.app.movie;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<MoviePayload> get() {

        log.trace("get all");
        return service.findAll();
    }

    @GetMapping("/{movieId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<MoviePayload> get(@PathVariable @NotNull Long movieId) {

        log.trace("get {}", movieId);
        return service.findById(movieId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MoviePayload> post(@RequestBody @NotNull MoviePayload moviePayload) {

        log.trace("post {}", moviePayload);
        return service.save(moviePayload);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> put(@RequestBody @NotNull MoviePayload moviePayload) {

        log.trace("put {}", moviePayload);
        return service.update(moviePayload);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable @NotNull Long id) {

        log.trace("delete {}", id);
        return service.deleteById(id);
    }

}
