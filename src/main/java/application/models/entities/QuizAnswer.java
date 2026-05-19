package application.models.entities;

import core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAnswer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "result_id", nullable = false)
    private QuizResult quizResult;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "word_id", nullable = false)
    private Vocabulary vocabulary;

    @Column(name = "user_answer", length = 255, nullable = false)
    private String userAnswer;

    @Column(name = "correct_answer", length = 255, nullable = false)
    private String correctAnswer;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect;
}