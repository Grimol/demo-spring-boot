package com.arthurrouelle.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlayerApplicationTests {
    
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnAPlayerWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("isis", "abc123")
                .getForEntity("/players/15", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(15);

        String pseudo = documentContext.read("$.pseudo");
        assertThat(pseudo).isEqualTo("IsisTheWarrior");
        
        String owner = documentContext.read("$.owner");
        assertThat(owner).isEqualTo("isis");
    }

    @Test
    void shouldNotReturnAPlayerWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("isis", "abc123")
                .getForEntity("/players/99999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    void shouldNotAllowAccessToPlayersTheyDoNotOwn() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("isis", "abc123")
                .getForEntity("/players/25", String.class); // 25 belongs to kumar2
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldCreateANewPlayer() {
        Player newPlayer = new Player(null, "NewWarrior", null, 0L, 1, 0L, 100.0, 10.0, 5.0, 1.0, 5.0, 150.0);
        
        ResponseEntity<Void> createResponse = restTemplate
                .withBasicAuth("isis", "abc123")
                .postForEntity("/players", newPlayer, Void.class);
        
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewPlayer = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate
                .withBasicAuth("isis", "abc123")
                .getForEntity(locationOfNewPlayer, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String pseudo = documentContext.read("$.pseudo");
        String owner = documentContext.read("$.owner");

        assertThat(id).isNotNull();
        assertThat(pseudo).isEqualTo("NewWarrior");
        assertThat(owner).isEqualTo("isis");
    }

    @Test
    void shouldReturnAllPlayersWhenListIsRequested() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("isis", "abc123")
                .getForEntity("/players", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray content = documentContext.read("$.content");
        assertThat(content.size()).isEqualTo(1);

        JSONArray ids = documentContext.read("$.content[*].id");
        assertThat(ids).containsExactlyInAnyOrder(15);

        JSONArray pseudos = documentContext.read("$.content[*].pseudo");
        assertThat(pseudos).containsExactlyInAnyOrder("IsisTheWarrior");
    }

    @Test
    void shouldReturnAPageOfPlayers() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("isis", "abc123")
                .getForEntity("/players?page=0&size=1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray content = documentContext.read("$.content");
        assertThat(content.size()).isEqualTo(1);
    }

    @Test
    void shouldReturnASortedPageOfPlayers() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("isis", "abc123")
                .getForEntity("/players?page=0&size=1&sort=pseudo,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray content = documentContext.read("$.content");
        assertThat(content.size()).isEqualTo(1);

        String pseudo = documentContext.read("$.content[0].pseudo");
        assertThat(pseudo).isEqualTo("IsisTheWarrior");
    }

    @Test
    void shouldReturnASortedPageOfPlayersWithNoParametersAndUseDefaultValues() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("isis", "abc123")
                .getForEntity("/players", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray content = documentContext.read("$.content");
        assertThat(content.size()).isEqualTo(1);

        JSONArray pseudos = documentContext.read("$.content[*].pseudo");
        assertThat(pseudos).containsExactlyInAnyOrder("IsisTheWarrior");
    }

    @Test
    void shouldNotReturnAPlayerWhenUsingBadCredentials() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("BAD-USER", "abc123")
                .getForEntity("/players/15", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        response = restTemplate
                .withBasicAuth("isis", "BAD-PASSWORD")
                .getForEntity("/players/15", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldRejectUsersWhoAreNotPlayersOwners() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("not-a-player", "qrs456")
                .getForEntity("/players", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldReturnDifferentPlayersForDifferentUsers() {
        ResponseEntity<String> isisResponse = restTemplate
                .withBasicAuth("isis", "abc123")
                .getForEntity("/players", String.class);
        assertThat(isisResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> lunaResponse = restTemplate
                .withBasicAuth("luna", "xyz789")
                .getForEntity("/players", String.class);
        assertThat(lunaResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext isisContext = JsonPath.parse(isisResponse.getBody());
        DocumentContext lunaContext = JsonPath.parse(lunaResponse.getBody());

        JSONArray isisPseudos = isisContext.read("$..pseudo");
        JSONArray lunaPseudos = lunaContext.read("$..pseudo");

        assertThat(isisPseudos).containsExactlyInAnyOrder("IsisTheWarrior");
        assertThat(lunaPseudos).containsExactlyInAnyOrder("LunaMage");
    }
}
