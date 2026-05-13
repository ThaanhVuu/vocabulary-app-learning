package application.models.entities;

import core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vocabularies")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, of = "english")
public class Vocabulary extends BaseEntity {

    @Column(nullable = false)
    private String vietnamese;

    @Column(nullable = false)
    private String english;

    @Column(columnDefinition = "TEXT")
    private String example;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = Topic.class)
    Topic topic;
}
