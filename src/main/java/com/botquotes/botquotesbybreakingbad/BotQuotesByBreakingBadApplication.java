package com.botquotes.botquotesbybreakingbad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BotQuotesByBreakingBadApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotQuotesByBreakingBadApplication.class, args);
    }

}
