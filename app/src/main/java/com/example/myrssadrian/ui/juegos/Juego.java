package com.example.myrssadrian.ui.juegos;

public class Juego {
    //declaracion de variables
    private int id;
    private String  nombre,plataforma, imagen;
    private String fecha;
    private float precio;
    //constructor
    public Juego(int id, String nombre, String plataforma, String imagen, String fecha, float precio) {
        this.id = id;
        this.nombre = nombre;
        this.plataforma = plataforma;
        this.imagen = imagen;
        this.fecha = fecha;
        this.precio = precio;
    }
    public Juego(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    //getters and setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getPlataforma() {
        return plataforma;
    }
    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }
    public String getImagen() {
        return imagen;
    }
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public float getPrecio() {
        return precio;
    }
    public void setPrecio(float precio) {
        this.precio = precio;
    }

}

