package application;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static DataManager instance;
    private List<TierList> ToutesLesTierLists = new ArrayList<>();
    private AppConfig config;
    private static final String FILE_PATH = "VosTierslist.ser";

    private DataManager() {
        config = ConfigManager.load();
        ToutesLesTierLists = chargerToutesLesTierLists();
    }

    public static DataManager getInstance() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    public AppConfig getConfig() {
        return config;
    }

    public void saveConfig() {
        ConfigManager.save(config);
    }

    public void addTierList(TierList tl) {
        ToutesLesTierLists.removeIf(tls -> tls.getName().equalsIgnoreCase(tl.getName()));
        ToutesLesTierLists.add(tl);
    }

    public List<TierList> getToutesLesTierLists() {
        return ToutesLesTierLists;
    }

    public void removeTierList(TierList tl) {
        ToutesLesTierLists.remove(tl);
        sauvegarderTout();
    }

    public TierList getTierlist(String name) {
        return ToutesLesTierLists.stream()
                .filter(tl -> tl.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public void enregistrerTiersList(TierList tierList) {
        addTierList(tierList);
        sauvegarderTout();
    }

    private void sauvegarderTout() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(ToutesLesTierLists);
            System.out.println("Toutes les TierLists sauvegardées !");
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde : " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<TierList> chargerToutesLesTierLists() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<TierList>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur chargement : " + e.getMessage());
            return new ArrayList<>();
        }
    }
}