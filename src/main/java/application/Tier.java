package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Tier implements Serializable {
    private static final long serialVersionUID = 1L;
    private final static int DEFAULT_HAUTEUR = 100;

    private TierList tierlist;
    private String name;
    private String color;
    private List<Item> items;
    private int hauteur;
    private int place;

    public Tier(String name, String color, int place, int hauteur) {
        this.name = name;
        this.color = color;
        this.items = new ArrayList<>();
        this.hauteur = hauteur;
        this.place = place;
    }

    public Tier(String name, String color, int place) {
        this(name, color, place, DEFAULT_HAUTEUR);
    }

    public Tier(String name, String color) {
        this(name, color, 0, DEFAULT_HAUTEUR);
    }

    public void setTierList(TierList tierList) {

        if (tierList != null) {
            tierlist = tierList;
        }
    }

    public TierList getTierList() {
        return tierlist;
    }

    public int getHauteur() {
        return hauteur;
    }

    public void setHauteur(int hauteur) {
        this.hauteur = hauteur;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
        if (tierlist != null) {
            tierlist.tri();
        }
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

    public void addItem(Item item) {
        if (item != null && !items.contains(item)) {
            items.add(item);
        }
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public void tri() {
        items.sort(Comparator.comparingInt(Item::getPlace));
    }

    @Override
    public String toString() {
        return "Tier{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", items=" + items +
                ", hauteur=" + hauteur +
                ", place=" + place +
                '}';
    }
}