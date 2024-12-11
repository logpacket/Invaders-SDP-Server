package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import message.Wallet;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "\"shop\"")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private long userId;

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

    public Shop(Wallet walletMessage, long userId) {
        this.coin = walletMessage.coin();
        this.bulletLevel = walletMessage.bulletLevel();
        this.shootLevel = walletMessage.shootLevel();
        this.livesLevel = walletMessage.livesLevel();
        this.coinLevel = walletMessage.coinLevel();
        this.userId = userId;
    }

    public void updateFromMessage(Wallet walletMessage) {
        this.coin = walletMessage.coin();
        this.bulletLevel = walletMessage.bulletLevel();
        this.shootLevel = walletMessage.shootLevel();
        this.livesLevel = walletMessage.livesLevel();
        this.coinLevel = walletMessage.coinLevel();
    }
}
