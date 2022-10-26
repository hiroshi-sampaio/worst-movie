package com.sampaio.hiroshi.worstmovie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        args = "--csv=src/test/resources/movielist.csv",
        properties = {
                "logging.level.com.sampaio.hiroshi.worstmovie=trace",
                "spring.profiles.active=test"})
class WorstMovieApplicationTest {

}