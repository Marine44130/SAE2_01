package application;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TMDBApiManager {

    // J'ai gardé le nom "searchMovieAsItem" pour ne pas casser ton Vue3Controller,
    // mais on cherche bien des jeux vidéo maintenant !
    public static Item searchMovieAsItem(String title, String apiKey) throws Exception {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new Exception("Clé API manquante. Veuillez la configurer dans l'accueil.");
        }

        OkHttpClient client = new OkHttpClient();

        // Encodage du texte (remplace les espaces par %20 pour l'URL)
        String query = URLEncoder.encode(title.trim(), StandardCharsets.UTF_8);

        // NOUVELLE URL : On attaque les serveurs de RAWG avec ta clé
        String url = "https://api.rawg.io/api/games?key=" + apiKey.trim() + "&search=" + query;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Pas de détails";
                throw new Exception("Erreur RAWG Code " + response.code() + " - " + errorBody);
            }

            String json = response.body().string();
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();

            // Vérifier si RAWG a trouvé des jeux
            if (root.getAsJsonArray("results").isEmpty()) {
                throw new Exception("Aucun jeu trouvé pour : " + title);
            }

            // Prendre le premier jeu de la liste
            JsonObject firstResult = root.getAsJsonArray("results").get(0).getAsJsonObject();

            // RAWG utilise "background_image" pour la jaquette/image du jeu
            if (firstResult.has("background_image") && !firstResult.get("background_image").isJsonNull()) {
                String imageUrl = firstResult.get("background_image").getAsString();
                return new Item(imageUrl, true); // On renvoie l'image
            } else {
                // Si le jeu n'a pas d'image, on renvoie son nom en texte
                String gameTitle = firstResult.get("name").getAsString();
                return new Item(gameTitle, false);
            }
        }
    }
}