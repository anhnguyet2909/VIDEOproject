package com.example.video;


public class HotVideos {
    int id;
    String title, avatar, file_mp4;

    public HotVideos(int id, String title, String avatar, String file_mp4) {
        this.id = id;
        this.title = title;
        this.avatar = avatar;
        this.file_mp4 = file_mp4;
    }

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFile_mp4() {
        return file_mp4;
    }

    public void setFile_mp4(String file_mp4) {
        this.file_mp4 = file_mp4;
    }

}
