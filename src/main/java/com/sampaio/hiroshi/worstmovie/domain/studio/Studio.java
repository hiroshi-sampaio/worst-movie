package com.sampaio.hiroshi.worstmovie.domain.studio;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;

@Data
@Builder
@Jacksonized
public class Studio {

    @Id
    private Long id;
    private String name;
}
