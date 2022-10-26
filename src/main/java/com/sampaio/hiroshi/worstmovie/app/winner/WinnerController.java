package com.sampaio.hiroshi.worstmovie.app.winner;

import com.sampaio.hiroshi.worstmovie.app.common.EntityDoesNotExistException;
import com.sampaio.hiroshi.worstmovie.app.common.IdMustBeGivenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import static java.util.Objects.isNull;

@Slf4j
@RestController
@RequestMapping("/winners")
@RequiredArgsConstructor
public class WinnerController {

    private final WinnerRepository repository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<Winner> get() {
        return repository.findAll();
    }

    @GetMapping("/{prizeYear}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Winner> get(@PathVariable @NotNull Integer prizeYear) {
        return repository.findByPrizeYear(prizeYear);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Winner> post(@RequestBody @NotNull Winner winner) {
        return repository.save(winner);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> put(@RequestBody @NotNull Winner winner) {

        if (isNull(winner.getPrizeYear())) {
            throw new IdMustBeGivenException();
        }

        return repository.findByPrizeYear(winner.getPrizeYear())
                .hasElement()
                .doOnNext(hasElement -> {
                    if (!hasElement) throw new EntityDoesNotExistException();
                })
                .then(repository.deleteByPrizeYear(winner.getPrizeYear()))
                .then(repository.save(winner))
                .then(Mono.empty());
    }

    @DeleteMapping("/{prizeYear}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable @NotNull Integer prizeYear) {
        return repository.deleteByPrizeYear(prizeYear);
    }
}
