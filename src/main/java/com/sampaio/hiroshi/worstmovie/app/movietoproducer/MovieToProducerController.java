package com.sampaio.hiroshi.worstmovie.app.movietoproducer;

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
@RequestMapping("/movie-to-producer")
@RequiredArgsConstructor
public class MovieToProducerController {

    private final MovieToProducerRepository repository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieToProducer> get() {

        log.trace("get all");
        return repository.findAll();
    }

    @GetMapping("/movie/{movieId}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieToProducer> getByMovieId(@PathVariable @NotNull Long movieId) {

        log.trace("get {}", movieId);
        return repository.findByMovieId(movieId);
    }

    @GetMapping("/producer/{producerId}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieToProducer> getByProducerId(@PathVariable @NotNull Long producerId) {

        log.trace("get {}", producerId);
        return repository.findByProducerId(producerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieToProducer> post(@RequestBody @NotNull MovieToProducer movieToProducer) {

        log.trace("post {}", movieToProducer);
        return repository.save(movieToProducer);
    }

    @DeleteMapping("/movie/{movieId}/producer/{producerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable @NotNull Long movieId, @PathVariable @NotNull Long producerId) {

        log.trace("delete by movie {} and producer {}", movieId, producerId);
        return repository.deleteByMovieIdAndProducerId(movieId, producerId);
    }

}
