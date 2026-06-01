package application;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static DataManager instance;
    private List<TierList> ToutesLesTierLists = new ArrayList<>();

    private DataManager() {}

    public static DataManager getInstance() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    public void addTierList(TierList tl) { ToutesLesTierLists.add(tl); }
    public List<TierList> getToutesLesTierLists() { return ToutesLesTierLists; }
    public void removeTierList(TierList tl) { ToutesLesTierLists.remove(tl); }
}