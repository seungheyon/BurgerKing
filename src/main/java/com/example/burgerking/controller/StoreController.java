package com.example.burgerking.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@RestController
public class StoreController {

    @GetMapping("/api/store")
    public String store() {
        String query = "버거킹";
        byte[] bytes = query.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        String encode = StandardCharsets.UTF_8.decode(buffer).toString();

        URI uri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com")
                .path("/v2/local/search/keyword.json")
                .queryParam("query", encode)
                .queryParam("size", 10)
                .encode()
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + "REST API 앱 키 값");
        RequestEntity<Void> req = RequestEntity
                .get(uri)
                .headers(headers)
                .build();
        ResponseEntity<String> result = restTemplate.exchange(req, String.class);
        return result.getBody();

    }
}