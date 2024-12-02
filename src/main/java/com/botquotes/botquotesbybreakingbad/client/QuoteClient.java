package com.botquotes.botquotesbybreakingbad.client;

import com.botquotes.botquotesbybreakingbad.dto.BreakingBadInfoDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QuoteClient {
    private static final String BREAKING_BAD_API_URL = "https://api.breakingbadquotes.xyz/v1/quotes";
    private final ObjectMapper objectMapper;
    private final Logger logger;

    public Optional<BreakingBadInfoDto> getRandomQuote() {
        List<BreakingBadInfoDto> breakingBadInfoDto = null;
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest build = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(BREAKING_BAD_API_URL))
                    .build();
            HttpResponse<String> send = httpClient.send(build, HttpResponse.BodyHandlers.ofString());
            breakingBadInfoDto = objectMapper.readValue(send.body(), new TypeReference<List<BreakingBadInfoDto>>() {});
        }
        catch(IOException| InterruptedException e){
            logger.warn("Can't get data");
        }
        return Optional.ofNullable(breakingBadInfoDto.get(0));
    }
}
