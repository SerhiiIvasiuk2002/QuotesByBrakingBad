package com.botquotes.botquotesbybreakingbad.mapper;

import com.botquotes.botquotesbybreakingbad.config.MapperConfig;
import com.botquotes.botquotesbybreakingbad.model.Character;
import com.botquotes.botquotesbybreakingbad.model.Quote;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface QuoteMapper {
    Quote toModel(String quote);
}
