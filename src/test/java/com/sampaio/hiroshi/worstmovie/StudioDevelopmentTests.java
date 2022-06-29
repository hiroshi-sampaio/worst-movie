package com.sampaio.hiroshi.worstmovie;

import com.sampaio.hiroshi.worstmovie.app.studio.Studio;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "logging.level.com.sampaio.hiroshi.worstmovie=trace")
class StudioDevelopmentTests {

    public static final String ROUTE = "/studios";
    @Autowired
    TestRestTemplate template;

    @Test
    void studios_all_flows_success() {
        var studio = Studio.builder().name("Studio Name").build();


        // Creation

        ResponseEntity<Studio> createdStudioResponse = template.postForEntity(ROUTE, studio, Studio.class);
        log.trace("Saved studio: {}", createdStudioResponse);

        assertThat(createdStudioResponse).isNotNull();
        assertThat(createdStudioResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createdStudioResponse.getBody()).isNotNull();
        assertThat(createdStudioResponse.getBody().getId()).isNotNull();


        // Fetching

        var resourceUri = new UriTemplate("{route}/{id}").expand(ROUTE, createdStudioResponse.getBody().getId());
        var fetchStudioResponse = template.getForEntity(resourceUri, Studio.class);

        assertThat(fetchStudioResponse).isNotNull();
        assertThat(fetchStudioResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(fetchStudioResponse.getBody()).isNotNull();
        assertThat(fetchStudioResponse.getBody()).usingRecursiveComparison().isEqualTo(createdStudioResponse.getBody());


        // Updating

        Studio changedStudio = fetchStudioResponse.getBody().toBuilder().name("Other Studio Name").build();

        ResponseEntity<Void> putResponse = template.exchange(ROUTE, HttpMethod.PUT, new HttpEntity<>(changedStudio), Void.class);

        assertThat(putResponse).isNotNull();
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);


        var fetchChangedStudioResponse = template.getForEntity(resourceUri, Studio.class);

        assertThat(fetchChangedStudioResponse).isNotNull();
        assertThat(fetchChangedStudioResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(fetchChangedStudioResponse.getBody()).isNotNull();
        assertThat(fetchChangedStudioResponse.getBody()).usingRecursiveComparison().isEqualTo(changedStudio);


        // Deleting

        ResponseEntity<Void> deleteResponse = template.exchange(resourceUri, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        assertThat(deleteResponse).isNotNull();
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var fetchDeletedStudioResponse = template.getForEntity(resourceUri, Studio.class);

        assertThat(fetchDeletedStudioResponse).isNotNull();
        assertThat(fetchDeletedStudioResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(fetchDeletedStudioResponse.getBody()).isNull();
    }

    @Test
    void studios_get_notPresent() {
        var resourceUri = new UriTemplate("{route}/{id}").expand(ROUTE, 1);

        var studioResponseEntity = template.getForEntity(resourceUri, Studio.class);

        assertThat(studioResponseEntity).isNotNull();
        assertThat(studioResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(studioResponseEntity.getBody()).isNull();
    }

}
