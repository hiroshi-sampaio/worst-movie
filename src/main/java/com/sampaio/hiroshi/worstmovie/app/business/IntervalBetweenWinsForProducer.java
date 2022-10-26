package com.sampaio.hiroshi.worstmovie.app.business;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IntervalBetweenWinsForProducer {

    private String producer;
    private Integer previousWin, followingWin;

    public Integer getInterval() {
        return followingWin - previousWin;
    }
}
