package com.ensa.projet.gatewayservice.controller;

import com.ensa.projet.gatewayservice.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final WebClient.Builder webClientBuilder;
    private final WebClient keycloakClient;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Value("${keycloak.token-uri}")
    private String tokenUri;

    @Value("${keycloak.userinfo-uri}")
    private String userInfoUri;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.redirect-uri}")
    private String redirectUri;

    public AuthController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
        this.keycloakClient = WebClient.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build();
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<AuthResponse>> register(@RequestParam String code) {
        return exchangeCodeForTokens(code)
                .flatMap(tokenResponse -> getUserInfo(tokenResponse)
                        .flatMap(userInfo -> createParticipant(userInfo)
                                .map(participant -> createAuthResponse(tokenResponse, participant))
                        ))
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    // Log the error here
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                });
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestParam String code) {
        log.info("Received login request with code: {}", code.substring(0, 10) + "...");

        return exchangeCodeForTokens(code)
                .doOnNext(tokenResponse -> {
                    log.info("Received token response: {}", tokenResponse);
                    if (tokenResponse.getAccessToken() == null) {
                        log.error("Access token is null");
                    } else {
                        log.info("Token exchange successful");
                    }
                })
                .doOnError(e -> log.error("Token exchange failed", e))
                .flatMap(tokenResponse -> getUserInfo(tokenResponse)
                        .doOnNext(user -> log.info("User info retrieved: {}", user.getSub()))
                        .doOnError(e -> log.error("Failed to get user info", e))
                        .flatMap(userInfo -> getParticipant(userInfo)
                                .doOnNext(p -> log.info("Participant retrieved: {}", p.getId()))
                                .doOnError(e -> log.error("Failed to get participant", e))
                                .map(participant -> createAuthResponse(tokenResponse, participant))
                        ))
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Authentication failed", e);
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.UNAUTHORIZED)
                            .body(new AuthResponse(null, null, null,
                                    "Authentication failed: " + e.getMessage())));
                });
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<AuthResponse>> refresh(@RequestParam String refreshToken) {
        return refreshAccessToken(refreshToken)
                .flatMap(tokenResponse -> getUserInfo(tokenResponse)
                        .flatMap(userInfo -> getParticipant(userInfo)
                                .map(participant -> createAuthResponse(tokenResponse, participant))
                        ))
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    // Log the error here
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                });
    }

    private Mono<TokenResponse> exchangeCodeForTokens(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("code", code);
        formData.add("redirect_uri", redirectUri);

        log.debug("Exchanging code for tokens with data: {}", formData);

        return keycloakClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .doOnError(e -> log.error("Token exchange request failed", e))
                .onErrorMap(e -> {
                    log.error("Token exchange error", e);
                    return new RuntimeException("Token exchange failed: " + e.getMessage());
                });
    }

    private Mono<TokenResponse> refreshAccessToken(String refreshToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("refresh_token", refreshToken);

        return keycloakClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(TokenResponse.class);
    }

    private Mono<KeycloakUserInfo> getUserInfo(TokenResponse tokenResponse) {
        return keycloakClient.get()
                .uri(userInfoUri)
                .headers(headers -> {
                    headers.setBearerAuth(tokenResponse.getAccessToken());
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                })
                .retrieve()
                .bodyToMono(KeycloakUserInfo.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Failed to get user info: {}", ex.getMessage());
                    return Mono.error(ex);
                });
    }

    private Mono<Participant> createParticipant(KeycloakUserInfo userInfo) {
        return webClientBuilder.build()
                .post()
                .uri("lb://participant-service/participants/save/" + userInfo.getSub())
                .bodyValue(userInfo)
                .retrieve()
                .bodyToMono(Participant.class);
    }

    private Mono<Participant> getParticipant(KeycloakUserInfo userInfo) {
        return webClientBuilder.build()
                .get()
                .uri("lb://participant-service/participants/get/" + userInfo.getSub())
                .retrieve()
                .bodyToMono(Participant.class);
    }

    private AuthResponse createAuthResponse(TokenResponse tokenResponse, Participant participant) {
        return AuthResponse.builder()
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .participantId(participant.getId())
                .userId(participant.getUserId())
                .build();
    }
}