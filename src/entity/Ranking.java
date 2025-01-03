package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "ranking")
public class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    @Setter
    @Column(name="high_score")
    private int highScore;

    public Ranking(User user, int highScore) {
        this.user = user;
        this.highScore = highScore;
    }
}
