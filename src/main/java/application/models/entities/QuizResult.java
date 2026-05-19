package application.models.entities;

import core.base.BaseEntity;
import core.utils.Searchable;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResult extends BaseEntity {

    @Searchable
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Searchable
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Searchable
    @Column(nullable = false)
    private int score;

    @Searchable
    @Column(name = "total_question", nullable = false)
    private int totalQuestion;

    @Searchable
    @Column(name = "time_taken_sec")
    private int timeTakenSec;

    @OneToMany(mappedBy = "quizResult", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuizAnswer> answers = new ArrayList<>();
}