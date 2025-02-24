package com.botquotes.botquotesbybreakingbad.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "characters")
public class Character { // TODO add character ukr name
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;
    @Column(unique = true, nullable = false)
    private String characterName;
    private String ukraineCharacterName;
   /* @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] imageByCharacter;

    */
    @OneToMany(mappedBy = "character", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private Set<Quote> quotes;
}
