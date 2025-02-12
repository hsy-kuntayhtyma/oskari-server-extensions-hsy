package hsy.seutumaisa.domain;

public class LandmassProject {

    private Long id;
    private String nimi;
    private String kunta;
    private int[] editors;
    private int[] managers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

}
