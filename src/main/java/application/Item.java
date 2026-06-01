package application;

import java.io.Serializable;

public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    private String content;
    private int place;
    private Tier tier;
    private boolean isImage;

    public Item(String text) {
        this(text, false);

    private boolean isImage;

    public Item(String text) {
        this.content = text;
        this.isImage = false;
    }

    public Item(String content, boolean isImage) {
        this.content = content;
        this.isImage = isImage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        this.isImage = image;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
        if (tier != null) {
            tier.tri();
        }
    }

    public Tier getTier() {
        return tier;
    }

    public void setTier(Tier nv_tier) {
        if(nv_tier != null){
            tier = nv_tier;
        }
    }

    @Override
    public String toString() {
        return (isImage ? "[IMG] " : "") + content;
    }
}
