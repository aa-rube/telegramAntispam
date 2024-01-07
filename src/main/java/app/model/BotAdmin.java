package app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "admins")
public class BotAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
