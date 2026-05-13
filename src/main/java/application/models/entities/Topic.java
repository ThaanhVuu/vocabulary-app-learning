package application.models.entities;

import core.base.BaseEntity;
import core.utils.Searchable;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "topics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Topic extends BaseEntity {
    @Column(nullable = false)
    @Searchable
    String name;

    @Searchable
    long wordCount;

    @Searchable
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, targetEntity = Vocabulary.class, fetch = FetchType.LAZY, orphanRemoval = true)
    @MapKey(name = "english")
    @Builder.Default
    Map<String, Vocabulary> vocabularies = new HashMap<>();

    public void addVocabulary(Vocabulary vocabulary) {
        if (this.vocabularies == null) {
            this.vocabularies = new HashMap<>();
        }

        this.vocabularies.put(vocabulary.getEnglish(), vocabulary);
        vocabulary.setTopic(this);
    }
}
