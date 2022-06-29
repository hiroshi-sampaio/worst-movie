package com.sampaio.hiroshi.worstmovie;

import com.sampaio.hiroshi.worstmovie.app.producer.Producer;
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
class ProducerDevelopmentTests {

    public static final String ROUTE = "/producers";
    @Autowired
    TestRestTemplate template;

    @Test
    void all_flows_success() {
        var producer = Producer.builder()
                .fistName("Producerfirstname")
                .middleNames("Producermiddlename1 Producermiddlename2")
                .lastName("Producerlastname")
                .build();


        // Creation

        ResponseEntity<Producer> createdProducerResponse = template.postForEntity(ROUTE, producer, Producer.class);
        log.trace("Saved producer: {}", createdProducerResponse);

        assertThat(createdProducerResponse).isNotNull();
        assertThat(createdProducerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createdProducerResponse.getBody()).isNotNull();
        assertThat(createdProducerResponse.getBody().getId()).isNotNull();


        // Fetching

        var resourceUri = new UriTemplate("{route}/{id}").expand(ROUTE, createdProducerResponse.getBody().getId());
        var fetchProducerResponse = template.getForEntity(resourceUri, Producer.class);

        assertThat(fetchProducerResponse).isNotNull();
        assertThat(fetchProducerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(fetchProducerResponse.getBody()).isNotNull();
        assertThat(fetchProducerResponse.getBody()).usingRecursiveComparison().isEqualTo(createdProducerResponse.getBody());


        // Updating

        Producer changedProducer = fetchProducerResponse.getBody().toBuilder()
                .fistName("Otherproducerfirstname")
                .middleNames("Otherproducermiddlename1 Producermiddlename2")
                .lastName("Otherproducerlastname")
                .build();

        ResponseEntity<Void> putResponse = template.exchange(ROUTE, HttpMethod.PUT, new HttpEntity<>(changedProducer), Void.class);

        assertThat(putResponse).isNotNull();
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);


        var fetchChangedProducerResponse = template.getForEntity(resourceUri, Producer.class);

        assertThat(fetchChangedProducerResponse).isNotNull();
        assertThat(fetchChangedProducerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(fetchChangedProducerResponse.getBody()).isNotNull();
        assertThat(fetchChangedProducerResponse.getBody()).usingRecursiveComparison().isEqualTo(changedProducer);


        // Deleting

        ResponseEntity<Void> deleteResponse = template.exchange(resourceUri, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        assertThat(deleteResponse).isNotNull();
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var fetchDeletedProducerResponse = template.getForEntity(resourceUri, Producer.class);

        assertThat(fetchDeletedProducerResponse).isNotNull();
        assertThat(fetchDeletedProducerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(fetchDeletedProducerResponse.getBody()).isNull();
    }

    @Test
    void get_notPresent() {
        var resourceUri = new UriTemplate("{route}/{id}").expand(ROUTE, 1);

        var responseEntity = template.getForEntity(resourceUri, Producer.class);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNull();
    }

}
