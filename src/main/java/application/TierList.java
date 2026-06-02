package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TierList implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<Tier> tiers;
    private List<Item> unrankedItems;

    public TierList(String name, List<Tier> tiers, List<Item> unrankedItems) {
        this.name = name;
        this.tiers = tiers;
        this.unrankedItems = unrankedItems;
    }

    public TierList(String name, List<Tier> tiers) {
        this(name, tiers, new ArrayList<>());
    }

    public TierList(String name) {
        this(name, new ArrayList<>(), new ArrayList<>());
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
            tier.setTierList(this);
        }
    }

    public void removeTier(Tier tier) {
        if (tier != null) {
            tiers.remove(tier);
        }
    }

    public void addUnrankedItem(Item item) {
        if (item != null && !unrankedItems.contains(item)) {
            unrankedItems.add(item);
        }
    }

    public void removeUnrankedItem(Item item) {
        unrankedItems.remove(item);
    }

    public void tri() {
        tiers.sort(Comparator.comparingInt(Tier::getPlace));
    }

    @Override
    public String toString() {
        return "TierList : " + name;
    }
}