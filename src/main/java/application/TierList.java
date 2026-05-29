package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TierList implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<Tier> tiers;
    private List<Item> unrankedItems; // Éléments "à classer"

    public TierList(String name) {
        this.name = name;
        this.tiers = new ArrayList<>();
        this.unrankedItems = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Tier> getTiers() {
        return tiers;
    }

    public List<Item> getUnrankedItems() {
        return unrankedItems;
    }

    public void addTier(Tier tier) {
        if (tier != null) {
            tiers.add(tier);
        }
    }

    public void removeTier(Tier tier) {
        tiers.remove(tier);
    }

    public void addUnrankedItem(Item item) {
        if (item != null && !unrankedItems.contains(item)) {
            unrankedItems.add(item);
        }
    }

    public void removeUnrankedItem(Item item) {
        unrankedItems.remove(item);
    }
}