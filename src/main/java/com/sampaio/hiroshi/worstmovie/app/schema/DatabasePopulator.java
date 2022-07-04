package com.sampaio.hiroshi.worstmovie.app.schema;

import com.sampaio.hiroshi.worstmovie.app.producer.Producer;
import com.sampaio.hiroshi.worstmovie.app.producer.ProducerRepository;
import com.sampaio.hiroshi.worstmovie.app.studio.Studio;
import com.sampaio.hiroshi.worstmovie.app.studio.StudioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@DependsOn("databaseSchemaCreator")
@Component
@RequiredArgsConstructor
public class DatabasePopulator implements ApplicationRunner {

    public static final int YEAR_COLUMN = 0;
    public static final int TITLE_COLUMN = 1;
    public static final int STUDIOS_COLUMN = 2;
    public static final int PRODUCERS_COLUMN = 3;

    @Value("classpath:movielist.csv")
    private final Resource csvResource;
    private final StudioRepository studioRepository;
    private final ProducerRepository producerRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        try (var inputStream = csvResource.getInputStream();
             var scanner = new Scanner(inputStream)) {
            while (scanner.hasNextLine()) {
                var line = scanner.nextLine();
                var cells = line.split(";");

                var movieYear = cells[YEAR_COLUMN];
                var movieTitle = cells[TITLE_COLUMN];

                var studios = cells[STUDIOS_COLUMN].split(",\\s*|\\s+and\\s+");

                Flux.fromArray(studios)
                        .map(studioName -> Studio.builder()
                                .name(studioName)
                                .build())
                        .map(Example::of)
                        .flatMap(studioRepository::findOne)
                        .doOnNext(studio -> studioToId.put(studio.getName(), studio.getId()))
                        .blockLast();

                var producers = cells[PRODUCERS_COLUMN].split(",\\s*|\\s+and\\s+");

                Flux.fromArray(producers)
                        .map(producerName -> {
                            String[] names = producerName.split("\\s+");
                            return Producer.builder()
                                    .fistName(names[0])
                                    .middleNames(IntStream
                                            .range(1, names.length - 1)
                                            .mapToObj(i -> names[i])
                                            .collect(Collectors.joining(" ")))
                                    .lastName(names[names.length - 1])
                                    .build();
                        })
                        .flatMap(producerRepository::save)
                        .blockLast();


            }
        }
        log.debug("Database populated");
    }
}
