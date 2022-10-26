package com.sampaio.hiroshi.worstmovie.app.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntervalBetweenWinsForProducer {

    private String producer;
    private Integer previousWin, followingWin;

    public Integer getInterval() {
        return followingWin - previousWin;
    }
}
