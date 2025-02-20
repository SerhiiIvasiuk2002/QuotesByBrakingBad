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
        for (int i = 0; i < 10; i++) {
            Optional<BreakingBadInfoDto> randomQuote = quoteClient.getRandomQuote();
            BreakingBadInfoDto breakingBadInfoDto = randomQuote.orElseThrow(() -> new RuntimeException("Can't get random quote"));
            Quote quoteModel = quoteMapper.toModel(breakingBadInfoDto.getQuote());
            Character characterModel = null;
            try {
                characterModel = characterMapper.toModel(breakingBadInfoDto.getAuthor(),
                        translatorUtil.translate(breakingBadInfoDto.getAuthor()),
                        imageUtil.findImage(breakingBadInfoDto.getAuthor()));
            } catch (ImageNotFoundException e) {
                logger.warn("Can't find image for {}", breakingBadInfoDto.getAuthor());
            }
            if(characterModel != null) {
                Optional<Character> byCharacterName =
                        characterRepository.findByCharacterName(characterModel.getCharacterName());
                Character finalCharacterModel = characterModel;
                Character character = byCharacterName.orElseGet(() -> characterRepository.save(finalCharacterModel));
                Set<Quote> quotes = character.getQuotes();
                if (quotes==null) {
                    quotes = new HashSet<>();
                }
                if(quoteModel.getQuote().length()>MAXSIZE ){
                    quoteModel.setQuote(quoteModel.getQuote().substring(0,MAXSIZE));
                }
                quoteModel.setCharacter(character);
                quoteRepository.save(quoteModel);
                quotes.add(quoteModel);
                character.setQuotes(quotes);
                characterRepository.save(character);
            }
        }
    }
}
