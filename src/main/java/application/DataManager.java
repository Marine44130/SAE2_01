package application;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static DataManager instance;
    private List<TierList> ToutesLesTierLists = new ArrayList<>();
    private AppConfig config;
    private static final String FILE_PATH = "tierlist_auto_save.ser";
    private DataManager() {
        config = ConfigManager.load();
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
        this.ToutesLesTierLists.add(tl);
    }

    public List<TierList> getToutesLesTierLists() {
        return ToutesLesTierLists;
    }

    public void removeTierList(TierList tl) {
        ToutesLesTierLists.remove(tl);
    }

    public TierList getTierlist (String name) {
        TierList trouve = null;
        for (TierList tl : ToutesLesTierLists){
            if (tl.getName() == name){
                trouve = tl;
            }
        }
        return trouve ;
    }

    public void enregistrerTiersList(TierList tierList){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(tierList);
            System.out.println("TierList sauvegardée !");
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde (fichier pas enregistré): " + e.getMessage());
        }
    }

    public TierList chargerTiersList(){
        File file = new File(FILE_PATH);

        if (!file.exists()){
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (TierList) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur d'import : " + e.getMessage());
            return null;
        }

    }
}