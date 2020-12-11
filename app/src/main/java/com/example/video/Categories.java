package com.example.video;

public class Categories {
    int id;
    String title, thumb;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Categories(int id, String title, String thumb) {
        this.id = id;
        this.title = title;
        this.thumb = thumb;
    }
}
