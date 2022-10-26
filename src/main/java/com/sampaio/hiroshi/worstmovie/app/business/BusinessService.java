package com.sampaio.hiroshi.worstmovie.app.business;

import com.sampaio.hiroshi.worstmovie.app.movietoproducer.MovieToProducer;
import com.sampaio.hiroshi.worstmovie.app.movietoproducer.MovieToProducerRepository;
import com.sampaio.hiroshi.worstmovie.app.producer.Producer;
import com.sampaio.hiroshi.worstmovie.app.producer.ProducerRepository;
import com.sampaio.hiroshi.worstmovie.app.winner.Winner;
import com.sampaio.hiroshi.worstmovie.app.winner.WinnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final ProducerRepository producerRepository;
    private final MovieToProducerRepository movieToProducerRepository;
    private final WinnerRepository winnerRepository;

    public Flux<Producer> producersWithPrizes() {
        return winnerRepository.findAll()
                .map(Winner::getMovieId)
                .flatMap(movieToProducerRepository::findByMovieId)
                .map(MovieToProducer::getProducerId).flatMap(producerRepository::findById);
    }

    public Mono<ProducersWithMaxAndMinIntervalBetweenPrizesResponse> producersWithMaxAndMinIntervalBetweenPrizes() {
        return winnerRepository.findAll()
                .flatMap(winner ->
                        movieToProducerRepository
                                .findByMovieId(winner.getMovieId())
                                .map(movieToProducer -> Tuples.of(movieToProducer.getProducerId(), winner.getPrizeYear()))
                )
                .groupBy(Tuple2::getT1)
                .flatMap(groupedFlux ->
                        groupedFlux.collect(
                                () -> Tuples.of(groupedFlux.key(), new ArrayList<Integer>()),
                                (acc, cur) -> acc.getT2().add(cur.getT2())))
                .filter(producerToYears -> producerToYears.getT2().size() > 1)
                .flatMap(producerToYears ->
                        producerRepository.findById(producerToYears.getT1())
                                .map(producer -> producerToYears.mapT1(ignored -> producer.getFullName())))
                .doOnNext(producerToYears -> Collections.sort(producerToYears.getT2()))
                .flatMap(producerToYears -> Flux.fromStream(IntStream
                        .range(1, producerToYears.getT2().size())
                        .mapToObj(index -> IntervalBetweenWinsForProducer.builder()
                                .producer(producerToYears.getT1())
                                .previousWin(producerToYears.getT2().get(index - 1))
                                .followingWin(producerToYears.getT2().get(index))
                                .build())))
                .filter(producerWinInterval -> producerWinInterval.getInterval() != 0)
                .collect(Collectors.groupingBy(IntervalBetweenWinsForProducer::getInterval))
                .map(map -> {
                    var keySet = map.keySet();
                    var min = Collections.min(keySet);
                    var max = Collections.max(keySet);
                    return ProducersWithMaxAndMinIntervalBetweenPrizesResponse.builder()
                            .min(map.get(min))
                            .max(map.get(max))
                            .build();
                });
    }


}
