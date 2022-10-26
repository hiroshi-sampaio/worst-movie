package com.sampaio.hiroshi.worstmovie.app.business;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class ProducersWithMaxAndMinIntervalBetweenPrizesResponse {
    private Collection<IntervalBetweenWinsForProducer> min;
    private Collection<IntervalBetweenWinsForProducer> max;
}
