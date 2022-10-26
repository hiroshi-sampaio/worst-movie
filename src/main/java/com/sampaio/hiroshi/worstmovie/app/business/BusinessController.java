package com.sampaio.hiroshi.worstmovie.app.business;

import com.sampaio.hiroshi.worstmovie.app.producer.Producer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService service;

    @GetMapping("producers-with-prizes")
    public Flux<Producer> producersWithPrizes() {
        return service.producersWithPrizes();
    }

    @GetMapping("producers-with-max-and-min-interval-between-prizes")
    public Mono<ProducersWithMaxAndMinIntervalBetweenPrizesResponse> producersWithMaxAndMinIntervalBetweenPrizes() {
        return service.producersWithMaxAndMinIntervalBetweenPrizes();
    }
}
