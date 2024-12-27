package hsy.seutumaisa.domain;

import java.util.Date;

public class LandMassArea {
    
    private Long id;
    private String nimi;
    private String osoite;
    private String geom;
    private String kohdetyyppi;
    private String vaihe;
    private String maamassatila;
    private Long omistaja_id;
    private Date alku_pvm;
    private Date loppu_pvm;
    private String lisatieto;
    private String kunta;
    private Integer status;
    
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

    public String getOsoite() {
        return osoite;
    }

    public void setOsoite(String osoite) {
        this.osoite = osoite;
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
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

    public String getMaamassan_tila() {
        return maamassatila;
    }

    public void setMaamassan_tila(String maamassatila) {
        this.maamassatila = maamassatila;
    }

    public Long getOmistaja_id() {
        return omistaja_id;
    }

    public void setOmistaja_id(Long omistaja_id) {
        this.omistaja_id = omistaja_id;
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

    public String getLisatieto() {
        return lisatieto;
    }

    public void setLisatieto(String lisatieto) {
        this.lisatieto = lisatieto;
    }

    public String getKunta() {
        return kunta;
    }

    public void setKunta(String kunta) {
        this.kunta = kunta;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
