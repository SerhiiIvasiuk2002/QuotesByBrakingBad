package com.botquotes.botquotesbybreakingbad.repository;

import com.botquotes.botquotesbybreakingbad.dto.CharacterDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.botquotes.botquotesbybreakingbad.model.Character;
import java.util.Optional;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {
    @Query("Select c FROM Character c WHERE c.characterName = :name" )
    Optional<Character> findByCharacterName (String name);
}
