package com.sampaio.hiroshi.worstmovie;

import com.sampaio.hiroshi.worstmovie.domain.studio.Studio;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "logging.level.com.sampaio.hiroshi.worstmovie=trace")
class WorstMovieApplicationTests {

    @Autowired
    TestRestTemplate template;

    @Test
    void studios_save_and_fetch_success() {
        var studio = Studio.builder().name("Studio Name").build();

        ResponseEntity<Studio> createdStudioResponse = template.postForEntity("/studios", studio, Studio.class);
        log.trace("Saved studio: {}", createdStudioResponse);

        assertThat(createdStudioResponse).isNotNull();
        assertThat(createdStudioResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createdStudioResponse.getBody()).isNotNull();
        assertThat(createdStudioResponse.getBody().getId()).isNotNull();

        var fetchUri = new UriTemplate("/studios/{id}").expand(createdStudioResponse.getBody().getId());
        var fetchStudioResponse = template.getForEntity(fetchUri, Studio.class);

        assertThat(fetchStudioResponse).isNotNull();
        assertThat(fetchStudioResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(createdStudioResponse.getBody()).usingRecursiveComparison().isEqualTo(fetchStudioResponse.getBody());
    }

    @Test
    void studios_get_notPresent() {
        var studioResponseEntity = template.getForEntity("/studios/1", Studio.class);

        assertThat(studioResponseEntity).isNotNull();
        assertThat(studioResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(studioResponseEntity.getBody()).isNull();
    }

}
