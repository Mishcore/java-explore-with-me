package ru.practicum.ewm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatsClient {

    private final RestTemplate restTemplate;
    private final String baseUri = "http://stats-server:9090";

    @Autowired
    public StatsClient() {
        this.restTemplate = new RestTemplate();
    }

    public ResponseEntity<EndpointHitDto> saveHit(EndpointHitDto hit) {
        log.info("Отправлен запрос на сохранение информации о запросе к эндпоинту {}", hit);
        String uri = baseUri + "/hit";
        return restTemplate.postForEntity(uri, hit, EndpointHitDto.class);
    }

    public ResponseEntity<List<ViewStats>> getStats(String start, String end, String[] uris, Boolean unique) {
        log.info("Отправлен запрос на получение статистики по посещениям");

        String uri = baseUri + "/stats";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                .queryParam("start", "{start}")
                .queryParam("end", "{end}")
                .queryParam("uris", "{uris}")
                .queryParam("unique", "{unique}");

        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("start", start);
        uriVariables.put("end", end);
        uriVariables.put("uris", uris);
        uriVariables.put("unique", unique);

        String expandedUriString = builder.buildAndExpand(uriVariables).toUriString();
        ParameterizedTypeReference<List<ViewStats>> responseType = new ParameterizedTypeReference<>() {};

        return restTemplate.exchange(expandedUriString, HttpMethod.GET, null, responseType);
    }
}
