package application;

import java.io.Serializable;

public class AppConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tmdbApiKey;
    private String rawgApiKey;

    public String getTmdbApiKey() {
        return tmdbApiKey;
    }

    public void setTmdbApiKey(String tmdbApiKey) {
        this.tmdbApiKey = tmdbApiKey;
    }

    public String getRawgApiKey() {
        return rawgApiKey;
    }

    public void setRawgApiKey(String rawgApiKey) {
        this.rawgApiKey = rawgApiKey;
    }
}