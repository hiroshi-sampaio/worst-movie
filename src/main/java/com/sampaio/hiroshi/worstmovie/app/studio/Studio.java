package com.sampaio.hiroshi.worstmovie.app.studio;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;

@Data
@Builder(toBuilder = true)
@Jacksonized
public class Studio {

    @Id
    private Long id;
    private String name;

    public static Studio of(final String name) {
        return new Studio(null, name);
    }
}
