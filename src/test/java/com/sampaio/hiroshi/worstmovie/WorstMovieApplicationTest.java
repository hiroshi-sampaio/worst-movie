package com.sampaio.hiroshi.worstmovie;

import com.sampaio.hiroshi.worstmovie.app.business.ProducersWithMaxAndMinIntervalBetweenPrizesResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        args = "--csv=src/test/resources/movielist.csv",
        properties = "logging.level.com.sampaio.hiroshi.worstmovie=trace")
class WorstMovieApplicationTest {

    @Autowired
    TestRestTemplate template;

    @Test
    void test() {
        var response = template.getForEntity(
                "/business/producers-with-max-and-min-interval-between-prizes",
                ProducersWithMaxAndMinIntervalBetweenPrizesResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.hasBody()).isTrue();

        var body = response.getBody();

        assertThat(body).isNotNull();

        assertThat(body.getMin()).hasSize(1);
        assertThat(body.getMin().get(0).getProducer()).isEqualTo("Joel Silver");
        assertThat(body.getMin().get(0).getPreviousWin()).isEqualTo(1990);
        assertThat(body.getMin().get(0).getFollowingWin()).isEqualTo(1991);
        assertThat(body.getMin().get(0).getInterval()).isOne();

        assertThat(body.getMax()).hasSize(1);
        assertThat(body.getMax().get(0).getProducer()).isEqualTo("Matthew Vaughn");
        assertThat(body.getMax().get(0).getPreviousWin()).isEqualTo(2002);
        assertThat(body.getMax().get(0).getFollowingWin()).isEqualTo(2015);
        assertThat(body.getMax().get(0).getInterval()).isEqualTo(13);
    }

}