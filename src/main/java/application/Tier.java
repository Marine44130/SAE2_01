package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tier implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String color; // Stocké au format Hexadécimal (ex: #FF0000)
    private List<Item> items;

    public Tier(String name, String color) {
        this.name = name;
        this.color = color;
        this.items = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Item> getItems() {
        return items;
    }

    // Ajoute un item dans ce Tier
    public void addItem(Item item) {
        if (item != null && !items.contains(item)) {
            items.add(item);
        }
    }

    // Retire un item de ce Tier (utilisé lors du Drag & Drop vers un autre niveau)
    public void removeItem(Item item) {
        items.remove(item);
    }
}