package com.fedkoroma.client.service;

import com.fedkoroma.client.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    public String getEmailFromToken(String token){

        if(token != null && token.startsWith("Bearer ")){
            token = token.substring(7);
        }

        String url = authServiceUrl + "/validate?token=" + token;
        ResponseEntity<AuthResponse> response = restTemplate.getForEntity(url, AuthResponse.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().isValid()) {
            return response.getBody().getEmail();
        } else {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Invalid token or unauthorized access");
        }
    }
}
