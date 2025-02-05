package hsy.seutumaisa.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LandMassArea {

    private Long id;

    private String geom;

    private String nimi;
    private String osoite;
    private String kunta;
    private String kohdetyyppi;
    private String vaihe;

    private Long omistaja_id;
    private String henkilo_nimi;
    private String henkilo_email;
    private String henkilo_puhelin;
    private String henkilo_organisaatio;

    private Date alku_pvm;
    private Date loppu_pvm;

    private List<LandMassData> data = new ArrayList<>();

    private Long hankealue_id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String name) {
        this.nimi = name;
    }

    public String getOsoite() {
        return osoite;
    }

    public void setOsoite(String osoite) {
        this.osoite = osoite;
    }

    public String getKunta() {
        return kunta;
    }

    public void setKunta(String kunta) {
        this.kunta = kunta;
    }

    public String getKohdetyyppi() {
        return kohdetyyppi;
    }

    public void setKohdetyyppi(String kohdetyyppi) {
        this.kohdetyyppi = kohdetyyppi;
    }

    public String getVaihe() {
        return vaihe;
    }

    public void setVaihe(String vaihe) {
        this.vaihe = vaihe;
    }

    public Long getOmistaja_id() {
        return omistaja_id;
    }

    public void setOmistaja_id(Long omistaja_id) {
        this.omistaja_id = omistaja_id;
    }

    public String getHenkilo_nimi() {
        return henkilo_nimi;
    }

    public void setHenkilo_nimi(String henkilo_nimi) {
        this.henkilo_nimi = henkilo_nimi;
    }

    public String getHenkilo_email() {
        return henkilo_email;
    }

    public void setHenkilo_email(String henkilo_email) {
        this.henkilo_email = henkilo_email;
    }

    public String getHenkilo_puhelin() {
        return henkilo_puhelin;
    }

    public void setHenkilo_puhelin(String henkilo_puhelin) {
        this.henkilo_puhelin = henkilo_puhelin;
    }

    public String getHenkilo_organisaatio() {
        return henkilo_organisaatio;
    }

    public void setHenkilo_organisaatio(String henkilo_organisaatio) {
        this.henkilo_organisaatio = henkilo_organisaatio;
    }

    public Date getAlku_pvm() {
        return alku_pvm;
    }

    public void setAlku_pvm(Date alku_pvm) {
        this.alku_pvm = alku_pvm;
    }

    public Date getLoppu_pvm() {
        return loppu_pvm;
    }

    public void setLoppu_pvm(Date loppu_pvm) {
        this.loppu_pvm = loppu_pvm;
    }

    public List<LandMassData> getData() {
        return data;
    }

    public void setData(List<LandMassData> data) {
        this.data = data;
    }

    public Long getHankealue_id() {
        return hankealue_id;
    }

    public void setHankealue_id(Long hankealue_id) {
        this.hankealue_id = hankealue_id;
    }

}
