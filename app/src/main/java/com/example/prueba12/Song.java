package com.example.prueba12;

public class Song {
    private long id;
    private String title;
    private String artist;
    private String filePath;
    private byte[] imageData;
    private boolean esFavorite;

    public Song(long id, String title, String artist, String filePath, boolean esFavorite) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
        this.esFavorite = esFavorite;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getFilePath() {
        return filePath;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public boolean getEsFavorite() {
        return esFavorite;
    }
    public void setEsFavorite(boolean esFavorite) {
        this.esFavorite = esFavorite;
    }
}
