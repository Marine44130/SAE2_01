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
            tier.setTierList(this);
            tri();
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

    public void tri(){
        tiers.sort(Comparator.comparingInt(Tier::getPlace));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════╗\n");
        sb.append("║  TierList : ").append(String.format("%-18s", name)).append("║\n");
        sb.append("╠══════════════════════════════╣\n");

        if (tiers.isEmpty()) {
            sb.append("║  Aucun tier défini           ║\n");
        } else {
            for (Tier tier : tiers) {
                sb.append("║  [").append(tier.getPlace()).append("] ")
                        .append(String.format("%-25s", tier.toString()))
                        .append("║\n");
            }
        }

        sb.append("╠══════════════════════════════╣\n");
        sb.append("║  Non classés (").append(String.format("%-2d", unrankedItems.size())).append(")           ║\n");

        if (!unrankedItems.isEmpty()) {
            for (Item item : unrankedItems) {
                sb.append("║    - ").append(String.format("%-25s", item.toString())).append("║\n");
            }
        }

        sb.append("╚══════════════════════════════╝");
        return sb.toString();
    }

    public int NbTiers() {
        return tiers.size();
    }
}