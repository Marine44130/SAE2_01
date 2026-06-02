package application;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MultiApiManager {

    private static final OkHttpClient client = new OkHttpClient();

    public static Item searchMovie(String title, String apiKey) throws Exception {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new Exception("Clé TMDB manquante.");
        }

        String query = URLEncoder.encode(title.trim(), StandardCharsets.UTF_8);
        String url = "https://api.themoviedb.org/3/search/movie?api_key=" + apiKey.trim() + "&query=" + query + "&language=fr-FR";

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Erreur TMDB (" + response.code() + ")");
            }

            JsonObject root = JsonParser.parseString(response.body().string()).getAsJsonObject();
            if (root.getAsJsonArray("results").isEmpty()) {
                throw new Exception("Aucun film trouvé pour : " + title);
            }

            JsonObject firstResult = root.getAsJsonArray("results").get(0).getAsJsonObject();

            if (firstResult.has("poster_path") && !firstResult.get("poster_path").isJsonNull()) {
                String posterPath = firstResult.get("poster_path").getAsString();
                return new Item("https://image.tmdb.org/t/p/w500" + posterPath, true);
            } else {
                return new Item(firstResult.get("title").getAsString(), false);
            }
        }
    }

    public static Item searchGame(String title, String apiKey) throws Exception {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new Exception("Clé RAWG manquante.");
        }

        String query = URLEncoder.encode(title.trim(), StandardCharsets.UTF_8);
        String url = "https://api.rawg.io/api/games?key=" + apiKey.trim() + "&search=" + query;

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Erreur RAWG (" + response.code() + ")");
            }

            JsonObject root = JsonParser.parseString(response.body().string()).getAsJsonObject();
            if (root.getAsJsonArray("results").isEmpty()) {
                throw new Exception("Aucun jeu trouvé pour : " + title);
            }

            JsonObject firstResult = root.getAsJsonArray("results").get(0).getAsJsonObject();

            if (firstResult.has("background_image") && !firstResult.get("background_image").isJsonNull()) {
                return new Item(firstResult.get("background_image").getAsString(), true);
            } else {
                return new Item(firstResult.get("name").getAsString(), false);
            }
        }
    }
}