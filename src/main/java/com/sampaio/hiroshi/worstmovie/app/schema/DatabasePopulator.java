package com.sampaio.hiroshi.worstmovie.app.schema;

import com.sampaio.hiroshi.worstmovie.app.movie.Movie;
import com.sampaio.hiroshi.worstmovie.app.movie.MovieRepository;
import com.sampaio.hiroshi.worstmovie.app.movie.MovieToProducer;
import com.sampaio.hiroshi.worstmovie.app.movie.MovieToProducerRepository;
import com.sampaio.hiroshi.worstmovie.app.movie.MovieToStudio;
import com.sampaio.hiroshi.worstmovie.app.movie.MovieToStudioRepository;
import com.sampaio.hiroshi.worstmovie.app.producer.Producer;
import com.sampaio.hiroshi.worstmovie.app.producer.ProducerRepository;
import com.sampaio.hiroshi.worstmovie.app.studio.Studio;
import com.sampaio.hiroshi.worstmovie.app.studio.StudioRepository;
import com.sampaio.hiroshi.worstmovie.app.winner.Winner;
import com.sampaio.hiroshi.worstmovie.app.winner.WinnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import reactor.core.CorePublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.function.Function;

@Slf4j
@DependsOn("databaseSchemaCreator")
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DatabasePopulator implements ApplicationRunner {

    public static final int YEAR_COLUMN = 0;
    public static final int TITLE_COLUMN = 1;
    public static final int STUDIOS_COLUMN = 2;
    public static final int PRODUCERS_COLUMN = 3;
    public static final int WINNER_COLUMN = 4;

    @Value("classpath:movielist.csv")
    private final Resource csvResource;
    private final StudioRepository studioRepository;
    private final ProducerRepository producerRepository;
    private final MovieRepository movieRepository;
    private final MovieToStudioRepository movieToStudioRepository;
    private final MovieToProducerRepository movieToProducerRepository;
    private final WinnerRepository winnerRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        final Collection<CorePublisher<?>> publishers = new ArrayList<>();

        try (var inputStream = csvResource.getInputStream();
             var scanner = new Scanner(inputStream)) {

            var ignored = scanner.nextLine(); // skip header

            while (scanner.hasNextLine()) {
                var line = scanner.nextLine();
                var cells = line.split(";");

                // Movie

                var movieTitle = cells[TITLE_COLUMN];
                var movieYear = Integer.parseInt(cells[YEAR_COLUMN]);

                var movieMono = Mono
                        .just(
                                Movie.builder()
                                        .title(movieTitle)
                                        .releaseYear(movieYear)
                                        .build())
                        .flatMap(movieRepository::save);

                var movieIdMono = Mono.from(movieMono).map(Movie::getId);

                // Studio

                var studios = cells[STUDIOS_COLUMN].split(",\\s*|\\s+and\\s+");

                var studioFlux = Flux.fromArray(studios)
                        .map(Studio::of)
                        .map(Example::of)
                        .flatMap(example ->
                                studioRepository
                                        .findOne(example)
                                        .switchIfEmpty(studioRepository.save(example.getProbe())))
                        .map(Studio::getId)
                        .zipWith(movieIdMono, (studioId, movieId) ->
                                MovieToStudio.builder()
                                        .studioId(studioId)
                                        .movieId(movieId)
                                        .build())
                        .flatMap(movieToStudioRepository::save);

                publishers.add(studioFlux);

                // Producer

                var producers = cells[PRODUCERS_COLUMN].split("(,\\s*|\\s+)?and\\s+|,\\s*");

                var producerFlux = Flux.fromArray(producers)
                        .map(Producer::of)
                        .map(Example::of)
                        .flatMap(example ->
                                producerRepository
                                        .findOne(example)
                                        .switchIfEmpty(producerRepository.save(example.getProbe())))
                        .map(Producer::getId)
                        .zipWith(movieIdMono, (producerId, movieId) ->
                                MovieToProducer.builder()
                                        .producerId(producerId)
                                        .movieId(movieId)
                                        .build())
                        .flatMap(movieToProducerRepository::save);

                publishers.add(producerFlux);

                // Winner

                var winnerFlag = cells.length == 5 && !cells[WINNER_COLUMN].isBlank();

                if (winnerFlag) {
                    var winnerMono = Mono.from(movieMono)
                            .map(movie -> Winner.builder()
                                    .prizeYear(movie.getReleaseYear())
                                    .movieId(movie.getId())
                                    .build())
                            .flatMap(winnerRepository::save);
                    publishers.add(winnerMono);
                }
            }
        }

        Flux.zip(publishers, Function.identity()).blockLast();

        log.debug("Database populated");
    }
}
