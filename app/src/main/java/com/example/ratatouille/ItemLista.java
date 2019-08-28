package com.example.ratatouille;

public class ItemLista {
    private String persona;
    private int imagen;
    private String desc;
    private String distance;

    public ItemLista(String persona, int imagen, String desc, String distance){
        this.desc = desc;
        this.distance = distance;
        this.imagen = imagen;
        this.persona = persona;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
