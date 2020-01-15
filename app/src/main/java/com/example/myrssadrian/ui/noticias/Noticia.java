package com.example.myrssadrian.ui.noticias;

public class Noticia {

    //declaracion de variables
    private String titulo, link, guid, autor, descripcion, fecha, contenido, imagen;

    //constructor
    public Noticia(String titulo, String link, String guid, String autor, String descripcion, String fecha, String contenido, String imagen) {
        this.titulo = titulo;
        this.link = link;
        this.guid = guid;
        this.autor = autor;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.contenido = contenido;
        this.imagen = imagen;
    }
    public Noticia() {
    }
    //Getters and Setters
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        this.guid = guid;
    }
    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public String getContenido() {
        return contenido;
    }
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
    public String getImagen() {
        return imagen;
    }
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

}
