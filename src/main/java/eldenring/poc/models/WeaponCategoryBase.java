package eldenring.poc.models;

public class WeaponCategoryBase extends BaseModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private String image;

    public WeaponCategoryBase() {}

    public WeaponCategoryBase(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getImageUrl() {
        return image;
    }

    @Override
    public String toString() {
        return "WeaponCategoryBase{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}

