package application;

import java.io.Serializable;

public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    private String content;

    private boolean isImage;

    public Item(String text) {
        this.content = text;
        this.isImage = false;
    }

    public Item(String content, boolean isImage) {
        this.content = content;
        this.isImage = isImage;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isImage() { return isImage; }
    public void setImage(boolean image) { isImage = image; }

    @Override
    public String toString() {
        return (isImage ? "[IMG] " : "") + content;
    }
}
