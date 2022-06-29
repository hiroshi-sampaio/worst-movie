package com.sampaio.hiroshi.worstmovie.domain.producer;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;

@Data
@Builder
@Jacksonized
public class Producer {

    @Id
    private Long id;
    private final String fistName, middleNames, lastName;
}
