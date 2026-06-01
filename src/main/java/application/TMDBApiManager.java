package application;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TMDBApiManager {

    public static Item searchMovieAsItem(String title, String apiKey) throws Exception {
        if (apiKey == null || apiKey.isBlank()) {
            throw new Exception("Clé API manquante. Veuillez la configurer dans l'accueil.");
        }

        OkHttpClient client = new OkHttpClient();
        String query = URLEncoder.encode(title.trim(), StandardCharsets.UTF_8);

        String url = "https://api.themoviedb.org/3/search/movie?query=" + query + "&api_key=" + apiKey.trim() + "&language=fr-FR";

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Pas de détails";
                throw new Exception("Code " + response.code() + " - " + errorBody);
            }

            String json = response.body().string();
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();

            if (root.getAsJsonArray("results").isEmpty()) {
                throw new Exception("Aucun film trouvé pour la recherche : " + title);
            }

            JsonObject firstResult = root.getAsJsonArray("results").get(0).getAsJsonObject();

            if (firstResult.has("poster_path") && !firstResult.get("poster_path").isJsonNull()) {
                String posterPath = firstResult.get("poster_path").getAsString();
                String urlImage = "https://image.tmdb.org/t/p/w500" + posterPath;
                return new Item(urlImage, true);
            } else {
                String movieTitle = firstResult.get("title").getAsString();
                return new Item(movieTitle, false);
            }
        }
    }
}