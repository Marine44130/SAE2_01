package application;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static DataManager instance;
    private List<TierList> ToutesLesTierLists = new ArrayList<>();
    private AppConfig config; // NOUVEAU

    private DataManager() {
        config = ConfigManager.load();
    }

    public static DataManager getInstance() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    public AppConfig getConfig() { return config; }

    public void saveConfig() { ConfigManager.save(config); }

    public void addTierList(TierList tl) { ToutesLesTierLists.add(tl); }
    public List<TierList> getToutesLesTierLists() { return ToutesLesTierLists; }
    public void removeTierList(TierList tl) { ToutesLesTierLists.remove(tl); }
}