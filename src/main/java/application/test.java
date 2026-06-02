package application;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    public static void main(String[] args) {
        TierList tierlist1 = new TierList("rank valo");
        Tier tier1 = new Tier("A", "#00000", 0);
        Tier tier2 = new Tier("B", "#00000", 1);
        Tier tier3 = new Tier("C", "#00000", 2);
        Tier tier4 = new Tier("D", "#00000", 3);

        tierlist1.addTier(tier1);
        tierlist1.addTier(tier2);
        tierlist1.addTier(tier3);
        tierlist1.addTier(tier4);

        tierlist1.removeTier(tier2);
        tier1.setPlace(5);
        tierlist1.tri();
        List<Tier> listtier1 = tierlist1.getTiers();

        System.out.println("État des Tiers : " + listtier1.toString());

        Item item1 = new Item("Or", false);
        Item item2 = new Item("", false);

        OkHttpClient client = new OkHttpClient();
        String jetonV4 = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJiNjg1YjNiZmE0MDljMDA3MGE1MTU4ZDlhOWJhYThhNSIsIm5iZiI6MTc4MDE1NzAwNi41NDQ5OTk4LCJzdWIiOiI2YTFiMGE0ZWIxNDg0YjQ5ZDQ5NzM4MzEiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.xm0NBheJuR2xNSbayHKaL04OgWcIZsr2KoSKQYVXGKI";
        String titre = "Hunger Games";
        String query = URLEncoder.encode(titre, StandardCharsets.UTF_8);
        Request request = new Request.Builder()
                .url("https://api.themoviedb.org/3/search/movie?query=" + query)
                .header("Authorization", "Bearer " + jetonV4)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String json = response.body().string();

            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            String posterPath = root.getAsJsonArray("results")
                    .get(0).getAsJsonObject()
                    .get("poster_path").getAsString();

            String urlImage = "https://image.tmdb.org/t/p/w500" + posterPath;
            Item itemHungerGames = new Item(urlImage, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}