package application.models.entities;

import core.base.BaseEntity;
import core.utils.Searchable;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
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
    String          name;

    @Searchable
    long            wordCount;

    @Searchable
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false)
    User            user;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, targetEntity = Vocabulary.class, fetch = FetchType.LAZY, orphanRemoval = true)
    Set<Vocabulary> vocabularies = new HashSet<>();
}
