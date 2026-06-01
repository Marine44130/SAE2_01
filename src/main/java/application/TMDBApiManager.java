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
        String query = URLEncoder.encode(title, StandardCharsets.UTF_8);

        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/search/movie?query=" + query)
                .header("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Erreur de l'API TMDB : " + response.code());
            }

            String json = response.body().string();
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();

            if (root.getAsJsonArray("results").isEmpty()) {
                throw new Exception("Aucun film trouvé pour : " + title);
            }

            String posterPath = root.getAsJsonArray("results")
                    .get(0).getAsJsonObject()
                    .get("poster_path").getAsString();

            String urlImage = "https://image.tmdb.org/t/p/w500" + posterPath;
            return new Item(urlImage, true);
        }
    }
}