package com.botquotes.botquotesbybreakingbad.repository;

import com.botquotes.botquotesbybreakingbad.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    @Query("Select q FROM Quote q JOIN fetch q.character WHERE q.quote = :quote")
    Optional<Quote> findByQuote(String quote);
}
