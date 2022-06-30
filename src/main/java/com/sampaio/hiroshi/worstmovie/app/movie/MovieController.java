package com.sampaio.hiroshi.worstmovie.app.movie;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService service;

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
    public Mono<ResponseEntity<Void>> put(@RequestBody @NotNull MoviePayload moviePayload) {
        return service.update(moviePayload);
/*
        return repository.existsById(moviePayload.getId())
                .doOnNext(exists -> {
                    if (!exists) throw new EntityDoesNotExistException();
                })
                .then(repository.save(mapper.toMovie(moviePayload)))
                .then(Mono.empty());
*/
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable @NotNull Long id) {

        return service.deleteById(id);
    }

}
