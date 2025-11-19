package eldenring.poc.models;

public class ItemBase extends BaseModel {
    private static final long serialVersionUID = 1L;

    private String title;
    private String image;

    public ItemBase() {}

    public ItemBase(String title, String image) {
        this.title = title;
        this.image = image;
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

    @Override
    public String getDisplayName() {
        return title;
    }

    @Override
    public String getImageUrl() {
        return image;
    }

    @Override
    public String toString() {
        return "ItemBase{" +
                "title='" + title + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}

