package app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "stop_word")
public class StopWordObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    String word;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getId() {
        return id;
    }
}
