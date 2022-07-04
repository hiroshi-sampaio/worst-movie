package com.sampaio.hiroshi.worstmovie.app.movie;

import com.sampaio.hiroshi.worstmovie.app.common.EntityDoesNotExistException;
import com.sampaio.hiroshi.worstmovie.app.common.IdMustBeEmptyException;
import com.sampaio.hiroshi.worstmovie.app.common.IdMustBeGivenException;
import com.sampaio.hiroshi.worstmovie.app.producer.Producer;
import com.sampaio.hiroshi.worstmovie.app.producer.ProducerRepository;
import com.sampaio.hiroshi.worstmovie.app.studio.Studio;
import com.sampaio.hiroshi.worstmovie.app.studio.StudioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieToStudioRepository movieToStudioRepository;
    private final StudioRepository studioRepository;
    private final MovieToProducerRepository movieToProducerRepository;
    private final ProducerRepository producerRepository;
    private final MovieMapper mapper;

    public Mono<MoviePayload> findById(Long movieId) {

        var studioListMono = movieToStudioRepository
                .findAll(
                        Example.of(
                                MovieToStudio.builder()
                                        .movieId(movieId)
                                        .build()))
                .map(MovieToStudio::getStudioId)
                .flatMap(studioRepository::findById)
                .collectList();

        var producerListMono = movieToProducerRepository
                .findAll(
                        Example.of(
                                MovieToProducer.builder()
                                        .movieId(movieId)
                                        .build()))
                .map(MovieToProducer::getProducerId)
                .flatMap(producerRepository::findById)
                .collectList();

        return movieRepository
                .findById(movieId)
                .map(mapper::toMoviePayload)
                .zipWith(studioListMono, (moviePayload, studios) -> moviePayload.toBuilder().studios(studios).build())
                .zipWith(producerListMono, (moviePayload, producers) -> moviePayload.toBuilder().producers(producers).build());
    }

    // TODO maybe implement automatic creation of producers and studios?
    public Mono<MoviePayload> save(MoviePayload moviePayload) {

        if (nonNull(moviePayload.getId())) {
            throw new IdMustBeEmptyException();
        }

        return Mono.just(moviePayload)
                .map(mapper::toMovie)
                .flatMap(movieRepository::save)
                .map(mapper::toMoviePayload)
                .zipWhen(movie ->
                        Mono.justOrEmpty(moviePayload.getStudios())
                                .flatMapMany(Flux::fromIterable)
                                .map(studio ->
                                        MovieToStudio.builder()
                                                .movieId(movie.getId())
                                                .studioId(studio.getId())
                                                .build())
                                .flatMap(movieToStudioRepository::save)
                                .map(MovieToStudio::getStudioId)
                                .flatMap(studioRepository::findById)
                                .collectList())
                .map(tuple ->
                        tuple.getT1().toBuilder()
                                .studios(tuple.getT2())
                                .build())
                .zipWhen(movie ->
                        Mono.justOrEmpty(moviePayload.getProducers())
                                .flatMapMany(Flux::fromIterable)
                                .map(producer ->
                                        MovieToProducer.builder()
                                                .movieId(movie.getId())
                                                .producerId(producer.getId())
                                                .build())
                                .flatMap(movieToProducerRepository::save)
                                .map(MovieToProducer::getProducerId)
                                .flatMap(producerRepository::findById)
                                .collectList())
                .map(tuple ->
                        tuple.getT1().toBuilder()
                                .producers(tuple.getT2())
                                .build());
    }

    public Mono<Void> update(MoviePayload moviePayload) {

        if (isNull(moviePayload.getId())) {
            throw new IdMustBeGivenException();
        }

        return movieRepository.existsById(moviePayload.getId())
                .doOnNext(exists -> {
                    if (!exists) throw new EntityDoesNotExistException();
                })
                .then(Mono.when(
                        movieRepository.save(mapper.toMovie(moviePayload)),
                        movieToStudioRepository.deleteByMovieId(moviePayload.getId()),
                        movieToProducerRepository.deleteByMovieId(moviePayload.getId())))
                .then(Mono.when(
                        movieToStudioRepository
                                .saveAll(
                                        StreamSupport
                                                .stream(moviePayload.getStudios().spliterator(), false)
                                                .map(Studio::getId)
                                                .map(studioId ->
                                                        MovieToStudio.builder()
                                                                .movieId(moviePayload.getId())
                                                                .studioId(studioId)
                                                                .build())
                                                .collect(Collectors.toList()))
                                .collectList(),
                        movieToProducerRepository
                                .saveAll(
                                        StreamSupport
                                                .stream(moviePayload.getProducers().spliterator(), false)
                                                .map(Producer::getId)
                                                .map(producerId ->
                                                        MovieToProducer.builder()
                                                                .movieId(moviePayload.getId())
                                                                .producerId(producerId)
                                                                .build())
                                                .collect(Collectors.toList()))
                                .collectList()));
    }

    public Mono<Void> deleteById(Long movieId) {

        if (isNull(movieId)) {
            throw new IdMustBeGivenException();
        }

        return movieRepository.existsById(movieId)
                .doOnNext(exists -> {
                    if (!exists) throw new EntityDoesNotExistException();
                })
                .then(Mono.when(
                        movieToStudioRepository.deleteByMovieId(movieId),
                        movieToProducerRepository.deleteByMovieId(movieId)))
                .then(movieRepository.deleteById(movieId));
    }
}
