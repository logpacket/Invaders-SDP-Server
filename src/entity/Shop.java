package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "\"shop\"")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;  // username 추가

    @Column(nullable = false)
    private int coin;

    @Column(nullable = false)
    private int bulletLevel;

    @Column(nullable = false)
    private int shootLevel;

    @Column(nullable = false)
    private int livesLevel;

    @Column(nullable = false)
    private int coinLevel;

    public Shop(message.Shop shopMessage) {
        this.username = shopMessage.username();  // username 초기화
        this.coin = shopMessage.coin();
        this.bulletLevel = shopMessage.bulletLevel();
        this.shootLevel = shopMessage.shootLevel();
        this.livesLevel = shopMessage.livesLevel();
        this.coinLevel = shopMessage.coinLevel();
    }

    public void updateFromMessage(message.Shop shopMessage) {
        this.coin = shopMessage.coin();
        this.bulletLevel = shopMessage.bulletLevel();
        this.shootLevel = shopMessage.shootLevel();
        this.livesLevel = shopMessage.livesLevel();
        this.coinLevel = shopMessage.coinLevel();
    }
}
