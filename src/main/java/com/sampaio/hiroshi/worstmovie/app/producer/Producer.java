package com.sampaio.hiroshi.worstmovie.app.producer;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;

@Data
@Builder(toBuilder = true)
@Jacksonized
public class Producer {

    @Id
    private Long id;
    private final String fistName, middleNames, lastName;
}
