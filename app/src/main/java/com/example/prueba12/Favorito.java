package com.example.prueba12;

public class Favorito {
    private long id;
    private String title;
    private String artista;
    private String data;

    public Favorito(long id, String title, String artista, String data) {
        this.id = id;
        this.title = title;
        this.artista = artista;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
