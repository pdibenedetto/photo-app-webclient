package com.appsdeveloperblog.ws.clients.photoappwebclient.controllers;

import java.util.Arrays;
import java.util.List;


import com.appsdeveloperblog.ws.clients.photoappwebclient.model.Album;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Controller
public class AlbumsController {

    private final OAuth2AuthorizedClientService oauthClientService;

    private final RestTemplate restTemplate;

    private final WebClient webClient;

    public AlbumsController(OAuth2AuthorizedClientService oauthClientService,
                            RestTemplate restTemplate,
                            WebClient webClient) {
        this.oauthClientService = oauthClientService;
        this.restTemplate = restTemplate;
        this.webClient = webClient;
    }

    @GetMapping("/albums-old")
    public String getAlbumsOld(Model model,
                            @AuthenticationPrincipal OidcUser principal,
                            Authentication authentication) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        OidcIdToken oidcIdToken = principal.getIdToken();
        String idToken = oidcIdToken.getTokenValue();

        log.info("Principal: {}", principal);
        log.info("OidcIdToken: {}", idToken);

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient oAuth2Client
                = oauthClientService.loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName());

        String jwtAccessToken = oAuth2Client.getAccessToken().getTokenValue();
        log.info("JWT Access Token: {}", jwtAccessToken);

        String apiGatewayAlbumsRouteUrl = "http://localhost:8082/albums";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtAccessToken);

        HttpEntity<List<Album>> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<List<Album>> responseEntity =
                restTemplate.exchange(apiGatewayAlbumsRouteUrl,
                        HttpMethod.GET,
                        httpEntity,
                        new ParameterizedTypeReference<>() {});



//        Album albumOne = Album.builder()
//                .albumId("album01")
//                .albumTitle("Album One Title")
//                .albumUrl("http://localhost:8082/albums/1")
//                .build();
//        Album albumTwo = Album.builder()
//                .albumId("album02")
//                .albumTitle("Album Two Title")
//                .albumUrl("http://localhost:8082/albums/2")
//                .build();
//        model.addAttribute("albums", Arrays.asList(albumOne, albumTwo));
        model.addAttribute("albums", responseEntity.getBody());
        return "albums";
    }

    @GetMapping("/albums")
    public String getAlbums(Model model,
                            @AuthenticationPrincipal OidcUser principal) {
        String apiGatewayAlbumsRouteUrl = "http://localhost:8082/albums";

        List<Album> albums = webClient.get()
                .uri(apiGatewayAlbumsRouteUrl)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Album>>(){})
                .block();

        model.addAttribute("albums", albums);
        return "albums";
    }

}
