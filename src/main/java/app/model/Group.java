package app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tg_groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "group_user_name")
    private String groupUserName;
    @Column(name = "owner_user_name")
    private String ownerUserName;
    @Column(name = "owner_chat_id")
    private Long ownerChatId;

    public int getId() {
        return id;
    }

    public String getGroupUserName() {
        return groupUserName;
    }

    public void setGroupUserName(String groupUserName) {
        this.groupUserName = groupUserName;
    }

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }

    public Long getOwnerChatId() {
        return ownerChatId;
    }

    public void setOwnerChatId(Long ownerChatId) {
        this.ownerChatId = ownerChatId;
    }

}

