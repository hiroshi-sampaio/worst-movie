package com.sampaio.hiroshi.worstmovie.app.producer;

import com.sampaio.hiroshi.worstmovie.app.common.EntityDoesNotExistException;
import com.sampaio.hiroshi.worstmovie.app.common.IdMustBeEmptyException;
import com.sampaio.hiroshi.worstmovie.app.common.IdMustBeGivenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RestController
@RequestMapping("/producers")
@RequiredArgsConstructor
public class ProducerController {

    private final ProducerRepository repository;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Producer> get(@PathVariable @NotNull Long id) {
        return repository.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Producer> post(@RequestBody @NotNull Producer producer) {

        if (nonNull(producer.getId())) {
            throw new IdMustBeEmptyException();
        }

        return repository.save(producer);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> put(@RequestBody @NotNull Producer producer) {

        if (isNull(producer.getId())) {
            throw new IdMustBeGivenException();
        }

        return repository.existsById(producer.getId())
                .doOnNext(exists -> {
                    if (!exists) throw new EntityDoesNotExistException();
                })
                .then(repository.save(producer))
                .then(Mono.empty());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable @NotNull Long id) {
        return repository.deleteById(id);
    }

}