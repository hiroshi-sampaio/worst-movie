package com.sampaio.hiroshi.worstmovie.app.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProducersWithMaxAndMinIntervalBetweenPrizesResponse {
    private List<IntervalBetweenWinsForProducer> min;
    private List<IntervalBetweenWinsForProducer> max;
}
