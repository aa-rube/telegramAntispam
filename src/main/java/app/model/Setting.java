package app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
@Entity
public class Setting {
    @Id
    private int id;
    private int countPhoto;
    private boolean checkPhoto;
    private int captionLength;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCountPhoto() {
        return countPhoto;
    }

    public void setCountPhoto(int countPhoto) {
        this.countPhoto = countPhoto;
    }

    public boolean isCheckPhoto() {
        return checkPhoto;
    }

    public void setCheckPhoto(boolean checkPhoto) {
        this.checkPhoto = checkPhoto;
    }

    public int getCaptionLength() {
        return captionLength;
    }

    public void setCaptionLength(int captionLength) {
        this.captionLength = captionLength;
    }
}
