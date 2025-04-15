package hsy.seutumaisa.domain;

public class Person {

    private Integer id;
    private String nimi;
    private String email;
    private String puhelin;
    private String organisaatio;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPuhelin() {
        return puhelin;
    }

    public void setPuhelin(String puhelin) {
        this.puhelin = puhelin;
    }

    public String getOrganisaatio() {
        return organisaatio;
    }

    public void setOrganisaatio(String organisaatio) {
        this.organisaatio = organisaatio;
    }

}
