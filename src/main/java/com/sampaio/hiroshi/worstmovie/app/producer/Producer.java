package com.sampaio.hiroshi.worstmovie.app.producer;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;

import java.util.Arrays;
import java.util.regex.Pattern;

@Data
@Builder(toBuilder = true)
@Jacksonized
public class Producer {

    @Id
    private Long id;
    private final String fistName, middleNames, lastName;

    public static Producer of(final String fullName) {
        var strings = Pattern.compile("\\s+").split(fullName);

        var builder = builder();

        builder.fistName(strings[0]);

        if (strings.length > 1) {
            builder.lastName(strings[strings.length - 1]);
        }

        if (strings.length > 2) {
            builder.middleNames(String.join(" ", Arrays.copyOfRange(strings, 1, strings.length - 1)));
        }

        return builder.build();
    }
}
