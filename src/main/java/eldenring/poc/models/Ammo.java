package eldenring.poc.models;

import java.util.List;

public class Ammo {
    private String id;
    private String name;
    private String image;
    private String description;
    private String type;
    private List<AttackPower> attackPower;
    private String passive;

    public Ammo() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AttackPower> getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(List<AttackPower> attackPower) {
        this.attackPower = attackPower;
    }

    public String getPassive() {
        return passive;
    }

    public void setPassive(String passive) {
        this.passive = passive;
    }
}

