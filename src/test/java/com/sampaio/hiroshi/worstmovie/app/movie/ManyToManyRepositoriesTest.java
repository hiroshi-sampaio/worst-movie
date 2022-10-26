package com.sampaio.hiroshi.worstmovie.app.movie;

import com.sampaio.hiroshi.worstmovie.app.movietoproducer.MovieToProducer;
import com.sampaio.hiroshi.worstmovie.app.movietoproducer.MovieToProducerRepository;
import com.sampaio.hiroshi.worstmovie.app.movietostudio.MovieToStudio;
import com.sampaio.hiroshi.worstmovie.app.movietostudio.MovieToStudioRepository;
import com.sampaio.hiroshi.worstmovie.app.producer.Producer;
import com.sampaio.hiroshi.worstmovie.app.producer.ProducerRepository;
import com.sampaio.hiroshi.worstmovie.app.studio.Studio;
import com.sampaio.hiroshi.worstmovie.app.studio.StudioRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        args = "--csv=src/test/resources/movielist.csv",
        properties = {
                "logging.level.com.sampaio.hiroshi.worstmovie=debug",
                "spring.profiles.active=test"})
class ManyToManyRepositoriesTest {

    @Autowired
    StudioRepository studioRepository;
    @Autowired
    ProducerRepository producerRepository;
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    MovieToStudioRepository movieToStudioRepository;
    @Autowired
    MovieToProducerRepository movieToProducerRepository;

    private <T> void logCreatedEntity(T entity) {
        log.trace("{} created: {}", entity.getClass().getSimpleName(), entity);
    }


    @BeforeEach
    void populateDatabase() {

        Flux.fromStream(
                        IntStream.rangeClosed(1, 3)
                                .mapToObj(i1 ->
                                        Studio.builder()
                                                .name("Studio " + i1)
                                                .build()))
                .flatMap(studioRepository::save)
                .doOnNext(this::logCreatedEntity)
                .blockLast();


        Flux.fromStream(
                        IntStream.rangeClosed(1, 5)
                                .mapToObj(i ->
                                        Producer.builder()
                                                .fistName("Producer")
                                                .middleNames("Number")
                                                .lastName(String.valueOf(i))
                                                .build()))
                .flatMap(producerRepository::save)
                .doOnNext(this::logCreatedEntity)
                .blockLast();


        Mono.just(
                        Movie.builder()
                                .title("Movie Title")
                                .releaseYear(2001)
                                .build())
                .flatMap(movieRepository::save)
                .doOnNext(this::logCreatedEntity)
                .block();
    }

    @AfterEach
    void clearDatabase() {
        movieToProducerRepository.deleteAll().block();
        movieToStudioRepository.deleteAll().block();
        studioRepository.deleteAll().block();
        movieRepository.deleteAll().block();
        producerRepository.deleteAll().block();
    }

    @Test
    void devTest() {

        // Save

        Flux.fromStream(
                        LongStream.rangeClosed(1, 3)
                                .mapToObj(i ->
                                        MovieToStudio.builder()
                                                .movieId(1L)
                                                .studioId(i)
                                                .build()))
                .flatMap(movieToStudioRepository::save)
                .doOnNext(this::logCreatedEntity)
                .blockLast();

        Flux.fromStream(
                        LongStream.rangeClosed(1, 5)
                                .mapToObj(i -> MovieToProducer.builder()
                                        .movieId(1L)
                                        .producerId(i)
                                        .build()))
                .flatMap(movieToProducerRepository::save)
                .doOnNext(this::logCreatedEntity)
                .blockLast();


        // Fetch

        var movieToStudioList = movieToStudioRepository.findAll(
                        Example.of(
                                MovieToStudio.builder()
                                        .movieId(1L)
                                        .build()))
                .doOnNext(movieToStudio -> log.trace("Fetch {}", movieToStudio))
                .collectList()
                .block();


        var movieToProducerList = movieToProducerRepository.findAll(
                        Example.of(
                                MovieToProducer.builder()
                                        .movieId(1L)
                                        .build()))
                .doOnNext(movieToProducer -> log.trace("Fetch {}", movieToProducer))
                .collectList()
                .block();

        assertThat(movieToStudioList).hasSize(3);
        assertThat(movieToProducerList).hasSize(5);


        // Delete

        movieToStudioRepository.findAll(
                        Example.of(
                                MovieToStudio.builder()
                                        .movieId(1L)
                                        .build()))
                .doOnNext(movieToStudio -> log.trace("Fetch {}", movieToStudio))
                .flatMap(movieToStudio ->
                        movieToStudioRepository
                                .deleteByMovieIdAndStudioId(
                                        movieToStudio.getMovieId(),
                                        movieToStudio.getStudioId()))
                .blockLast();

        var deletedMovieToStudioList = movieToStudioRepository.findAll(
                        Example.of(
                                MovieToStudio.builder()
                                        .movieId(1L)
                                        .build()))
                .doOnNext(movieToStudio -> log.trace("Fetch {}", movieToStudio))
                .collectList()
                .block();

        assertThat(deletedMovieToStudioList).isEmpty();


        movieToProducerRepository.findAll(
                        Example.of(MovieToProducer.builder()
                                .movieId(1L)
                                .build()))
                .doOnNext(movieToProducer -> log.trace("Fetch {}", movieToProducer))
                .flatMap(movieToProducer ->
                        movieToProducerRepository
                                .deleteByMovieIdAndProducerId(
                                        movieToProducer.getMovieId(),
                                        movieToProducer.getProducerId()))
                .blockLast();

        var deletedMovieToProducerList = movieToProducerRepository.findAll(
                        Example.of(MovieToProducer.builder()
                                .movieId(1L)
                                .build()))
                .doOnNext(movieToProducer -> log.trace("Fetch {}", movieToProducer))
                .collectList()
                .block();

        assertThat(deletedMovieToProducerList).isEmpty();
    }
}