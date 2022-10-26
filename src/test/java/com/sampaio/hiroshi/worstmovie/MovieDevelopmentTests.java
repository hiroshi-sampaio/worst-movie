package com.sampaio.hiroshi.worstmovie;

import com.sampaio.hiroshi.worstmovie.app.movie.MoviePayload;
import com.sampaio.hiroshi.worstmovie.app.movie.MovieRepository;
import com.sampaio.hiroshi.worstmovie.app.movietoproducer.MovieToProducerRepository;
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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriTemplate;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        args = "--csv=src/test/resources/movielist.csv",
        properties = {
                "logging.level.com.sampaio.hiroshi.worstmovie=trace",
                "spring.profiles.active=test"})
class MovieDevelopmentTests {

    public static final String STUDIOS_ROUTE = "/studios";
    public static final String PRODUCERS_ROUTE = "/producers";
    public static final String MOVIES_ROUTE = "/movies";
    @Autowired
    TestRestTemplate template;
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


    @BeforeEach
    void populateDatabase() {

        IntStream.rangeClosed(1, 3)
                .mapToObj(i ->
                        Studio.builder()
                                .name("Studio " + i)
                                .build())
                .map(studio -> template.postForEntity(STUDIOS_ROUTE, studio, Studio.class))
                .forEach(studioResponseEntity -> {
                    assertThat(studioResponseEntity).isNotNull();
                    assertThat(studioResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

                    log.trace("Created studio: {}", studioResponseEntity.getBody());
                });


        IntStream.rangeClosed(1, 5)
                .mapToObj(i ->
                        Producer.builder()
                                .fistName("Producer")
                                .middleNames("Number")
                                .lastName(String.valueOf(i))
                                .build())
                .map(producer -> template.postForEntity(PRODUCERS_ROUTE, producer, Producer.class))
                .forEach(producerResponseEntity -> {
                    assertThat(producerResponseEntity).isNotNull();
                    assertThat(producerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

                    log.trace("Created producer: {}", producerResponseEntity.getBody());
                });


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
    void all_flows_success() {

        var moviePayload = MoviePayload.builder()
                .title("Movie Title")
                .releaseYear(2001)
                .studios(
                        LongStream.rangeClosed(1, 3)
                                .mapToObj(i -> Studio.builder().id(i).build())
                                .collect(Collectors.toUnmodifiableList()))
                .producers(
                        LongStream.rangeClosed(1, 5)
                                .mapToObj(i -> Producer.builder().id(i).build())
                                .collect(Collectors.toUnmodifiableList()))
                .build();

        log.trace("Movie entity: {}", moviePayload);

        // Creation

        ResponseEntity<MoviePayload> createdMovieResponse = template.postForEntity(MOVIES_ROUTE, moviePayload, MoviePayload.class);
        log.trace("createdMovieResponse.getBody(): {}", createdMovieResponse.getBody());

        assertThat(createdMovieResponse).isNotNull();
        assertThat(createdMovieResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createdMovieResponse.getBody()).isNotNull();
        assertThat(createdMovieResponse.getBody().getId()).isNotNull();

        // Fetching

        var resourceUri = new UriTemplate("{route}/{id}").expand(MOVIES_ROUTE, createdMovieResponse.getBody().getId());
        var fetchMovieResponse = template.getForEntity(resourceUri, MoviePayload.class);
        log.trace("fetchMovieResponse.getBody(): {}", fetchMovieResponse.getBody());

        assertThat(fetchMovieResponse).isNotNull();
        assertThat(fetchMovieResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(fetchMovieResponse.getBody()).isNotNull();
        assertThat(fetchMovieResponse.getBody()).usingRecursiveComparison().isEqualTo(createdMovieResponse.getBody());


        // Updating

        MoviePayload fetchMoviePayload = fetchMovieResponse.getBody();

        MoviePayload changedMovie = fetchMoviePayload.toBuilder()
                .title("Changed " + fetchMoviePayload.getTitle())
                .studios(
                        StreamSupport
                                .stream(fetchMoviePayload.getStudios().spliterator(), false)
                                .filter(studio -> studio.getId() < 3)
                                .collect(Collectors.toUnmodifiableList()))
                .producers(
                        StreamSupport
                                .stream(fetchMoviePayload.getProducers().spliterator(), false)
                                .filter(producer -> producer.getId() < 4)
                                .collect(Collectors.toUnmodifiableList()))
                .build();

        log.trace("Changed Movie: {}", changedMovie);

        ResponseEntity<Void> putResponse = template.exchange(MOVIES_ROUTE, HttpMethod.PUT, new HttpEntity<>(changedMovie), Void.class);

        assertThat(putResponse).isNotNull();
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var fetchChangedMovieResponse = template.getForEntity(resourceUri, MoviePayload.class);

        assertThat(fetchChangedMovieResponse).isNotNull();
        assertThat(fetchChangedMovieResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(fetchChangedMovieResponse.getBody()).isNotNull();
        assertThat(changedMovie).usingRecursiveComparison().isEqualTo(fetchChangedMovieResponse.getBody());


        // Deleting

        ResponseEntity<Void> deleteResponse = template.exchange(resourceUri, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        assertThat(deleteResponse).isNotNull();
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var fetchDeletedMovieResponse = template.getForEntity(resourceUri, MoviePayload.class);

        assertThat(fetchDeletedMovieResponse).isNotNull();
        assertThat(fetchDeletedMovieResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(fetchDeletedMovieResponse.getBody()).isNull();
    }
}
