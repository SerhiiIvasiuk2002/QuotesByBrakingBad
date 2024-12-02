package com.botquotes.botquotesbybreakingbad.mapper;
import com.botquotes.botquotesbybreakingbad.config.MapperConfig;
import org.mapstruct.Mapper;
import com.botquotes.botquotesbybreakingbad.model.Character;

@Mapper(config = MapperConfig.class)
public interface CharacterMapper {
    Character toModel(String characterName,String ukraineCharacterName, String imageByCharacter);
}
