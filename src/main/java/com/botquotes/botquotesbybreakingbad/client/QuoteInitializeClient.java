package com.botquotes.botquotesbybreakingbad.client;

import com.botquotes.botquotesbybreakingbad.dto.BreakingBadInfoDto;
import com.botquotes.botquotesbybreakingbad.exception.ImageNotFoundException;
import com.botquotes.botquotesbybreakingbad.mapper.CharacterMapper;
import com.botquotes.botquotesbybreakingbad.mapper.QuoteMapper;
import com.botquotes.botquotesbybreakingbad.model.Quote;
import com.botquotes.botquotesbybreakingbad.model.Character;
import com.botquotes.botquotesbybreakingbad.repository.CharacterRepository;
import com.botquotes.botquotesbybreakingbad.repository.QuoteRepository;
import com.botquotes.botquotesbybreakingbad.util.ImageUtil;
import com.botquotes.botquotesbybreakingbad.util.TranslatorUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class QuoteInitializeClient {
    private final QuoteClient quoteClient;
    private final QuoteMapper quoteMapper;
    private final CharacterMapper characterMapper;
    private final ImageUtil imageUtil;
    private final Logger logger;
    private final CharacterRepository characterRepository;
    private final QuoteRepository quoteRepository;
    private final TranslatorUtil translatorUtil;
    private final Integer MAXSIZE = 255;

    @Scheduled(initialDelay = 5000, fixedRate = 3600000)
    private void initializeQuote() {
        for (int i = 0; i < 4; i++) {
            Optional<BreakingBadInfoDto> randomQuote = quoteClient.getRandomQuote();
            BreakingBadInfoDto breakingBadInfoDto = randomQuote.orElseThrow(() -> new RuntimeException("Can't get random quote"));
            Quote quoteModel = quoteMapper.toModel(breakingBadInfoDto.getQuote());
            Character characterModel = null;
            characterModel = characterMapper.toModel(breakingBadInfoDto.getAuthor(), translatorUtil.translate(breakingBadInfoDto.getAuthor()));
            CheckCharacterIsNotNullAndSave(characterModel,quoteModel);
        }
    }
    private void CheckCharacterIsNotNullAndSave(Character character, Quote quote) {
        if(character != null) {
            Optional<Character> byCharacterName =
                    characterRepository.findByCharacterName(character.getCharacterName());
            Character finalCharacterModel = character;
            Character characterModel = byCharacterName.orElseGet(() -> characterRepository.save(finalCharacterModel));
            Set <Quote> quotes = quoteRepository.findQuotesByCharacterName(characterModel.getCharacterName());
            CheckQuoteIsEmpty(quotes);
            CheckQuoteLength(quote);
            quote.setCharacter(characterModel);
            quoteRepository.save(quote);
            quotes.add(quote);
            characterModel.setQuotes(quotes);
            characterRepository.save(characterModel);
        }
    }

    private void CheckQuoteIsEmpty(Set<Quote> q){
        if (q==null || q.isEmpty()) {
            q = new HashSet<>();
        }
    }
    private void CheckQuoteLength(Quote quote){
        if(quote.getQuote().length()>MAXSIZE ){
            quote.setQuote(quote.getQuote().substring(0,MAXSIZE));
        }
    }
}
