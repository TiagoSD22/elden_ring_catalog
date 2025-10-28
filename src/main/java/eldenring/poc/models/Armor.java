package eldenring.poc.models;

/**
 * Full armor information.
 * Currently only contains basic fields, can be extended with more details.
 */
public class Armor {
    private String id;
    private String title;
    private String image;
    private String description;

    public Armor() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

