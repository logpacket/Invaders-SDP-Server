package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String userid;
    private int highscore;

    public Ranking() {}

    public Ranking(String userid, int highscore) {
        this.userid = userid;
        this.highscore = highscore;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserid() { return userid; }
    public void setUserid(String userid) { this.userid = userid; }

    public int getHighscore() { return highscore; }
    public void setHighscore(int highscore) { this.highscore = highscore; }
}
