package hsy.seutumaisa.domain;

import java.util.stream.IntStream;

public class LandmassProject {

    private Integer id;
    private String nimi;
    private String kunta;
    private int[] editors;
    private int[] managers;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String name) {
        this.nimi = name;
    }

    public String getKunta() {
        return kunta;
    }

    public void setKunta(String kunta) {
        this.kunta = kunta;
    }

    public int[] getEditors() {
        return editors;
    }

    public void setEditors(int[] editors) {
        this.editors = editors;
    }

    public int[] getManagers() {
        return managers;
    }

    public void setManagers(int[] managers) {
        this.managers = managers;
    }

    public boolean isInEditors(int userId) {
        return editors != null && IntStream.of(editors).anyMatch(x -> x == userId);
    }

    public boolean isInManagers(int userId) {
        return managers != null && IntStream.of(managers).anyMatch(x -> x == userId);
    }

}
