package eldenring.poc.models;

import java.io.Serializable;

/**
 * Basic ammo information scraped from the wiki gallery.
 * Contains only image and name for the listing view.
 */
public class AmmoBase implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String image;

    public AmmoBase() {}

    public AmmoBase(String name, String image) {
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
    public String toString() {
        return "AmmoBase{" +
                "name='" + name + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}

