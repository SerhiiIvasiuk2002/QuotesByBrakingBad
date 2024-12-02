package com.botquotes.botquotesbybreakingbad.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "characters")
public class Character { // TODO add character ukr name
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private int id;
    @Column(unique = true, nullable = false)
    private String characterName;
    private String ukraineCharacterName;
    @Lob
    private String imageByCharacter;
    @OneToMany(mappedBy = "character", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private List<Quote> quotes;
}
