package hsy.seutumaisa.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LandmassData {

    @JsonProperty("maamassatieto_id")
    private Integer id;

    @JsonProperty("maamassakohde_id")
    private Integer maamassakohde_id;

    @JsonProperty("maamassan_ryhma")
    private String maamassaryhma;

    @JsonProperty("maamassan_laji")
    private String maamassalaji;

    @JsonProperty("kelpoisuusluokkaryhma")
    private String kelpoisuusluokkaryhma;

    @JsonProperty("kelpoisuusluokka")
    private String kelpoisuusluokka;

    @JsonProperty("maamassan_tila")
    private String maamassatila;

    @JsonProperty("tiedontuottaja")
    private String tiedontuottaja;

    @JsonProperty("planned_begin_date")
    private Date planned_begin_date;

    @JsonProperty("planned_end_date")
    private Date planned_end_date;

    @JsonProperty("amount_remaining")
    private Integer amount_remaining;

    @JsonProperty("lisatieto")
    private String lisatieto;

    @JsonProperty("liitteet")
    private String liitteet;

    @JsonProperty("varattu")
    private Boolean varattu;

    @JsonProperty("muokattu")
    private Date muokattu;

    @JsonProperty("luotu")
    private Date luotu;

    @JsonProperty("realized_begin_date")
    private String realized_begin_date;

    @JsonProperty("realized_end_date")
    private String realized_end_date;

    @JsonProperty("pilaantuneisuus")
    private String pilaantuneisuus;

    @JsonProperty("tiedon_luotettavuus")
    private String tiedon_luotettavuus;

    @JsonProperty("amount_total")
    private Integer amount_total;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMaamassakohde_id() {
        return maamassakohde_id;
    }

    public void setMaamassakohde_id(Integer maamassakohde_id) {
        this.maamassakohde_id = maamassakohde_id;
    }

    public String getMaamassaryhma() {
        return maamassaryhma;
    }

    public void setMaamassaryhma(String maamassaryhma) {
        this.maamassaryhma = maamassaryhma;
    }

    public String getMaamassalaji() {
        return maamassalaji;
    }

    public void setMaamassalaji(String maamassalaji) {
        this.maamassalaji = maamassalaji;
    }

    public String getKelpoisuusluokkaryhma() {
        return kelpoisuusluokkaryhma;
    }

    public void setKelpoisuusluokkaryhma(String kelpoisuusluokkaryhma) {
        this.kelpoisuusluokkaryhma = kelpoisuusluokkaryhma;
    }

    public String getKelpoisuusluokka() {
        return kelpoisuusluokka;
    }

    public void setKelpoisuusluokka(String kelpoisuusluokka) {
        this.kelpoisuusluokka = kelpoisuusluokka;
    }

    public String getMaamassatila() {
        return maamassatila;
    }

    public void setMaamassatila(String maamassatila) {
        this.maamassatila = maamassatila;
    }

    public String getTiedontuottaja() {
        return tiedontuottaja;
    }

    public void setTiedontuottaja(String tiedontuottaja) {
        this.tiedontuottaja = tiedontuottaja;
    }

    public Date getPlanned_begin_date() {
        return planned_begin_date;
    }

    public void setPlanned_begin_date(Date planned_begin_date) {
        this.planned_begin_date = planned_begin_date;
    }

    public Date getPlanned_end_date() {
        return planned_end_date;
    }

    public void setPlanned_end_date(Date planned_end_date) {
        this.planned_end_date = planned_end_date;
    }

    public Integer getAmount_remaining() {
        return amount_remaining;
    }

    public void setAmount_remaining(Integer amount_remaining) {
        this.amount_remaining = amount_remaining;
    }

    public String getLisatieto() {
        return lisatieto;
    }

    public void setLisatieto(String lisatieto) {
        this.lisatieto = lisatieto;
    }

    public String getLiitteet() {
        return liitteet;
    }

    public void setLiitteet(String liitteet) {
        this.liitteet = liitteet;
    }

    public Boolean getVarattu() {
        return varattu;
    }

    public void setVarattu(Boolean varattu) {
        this.varattu = varattu;
    }

    public Date getMuokattu() {
        return muokattu;
    }

    public void setMuokattu(Date muokattu) {
        this.muokattu = muokattu;
    }

    public Date getLuotu() {
        return luotu;
    }

    public void setLuotu(Date luotu) {
        this.luotu = luotu;
    }

    public String getRealized_begin_date() {
        return realized_begin_date;
    }

    public void setRealized_begin_date(String realized_begin_date) {
        this.realized_begin_date = realized_begin_date;
    }

    public String getRealized_end_date() {
        return realized_end_date;
    }

    public void setRealized_end_date(String realized_end_date) {
        this.realized_end_date = realized_end_date;
    }

    public String getPilaantuneisuus() {
        return pilaantuneisuus;
    }

    public void setPilaantuneisuus(String pilaantuneisuus) {
        this.pilaantuneisuus = pilaantuneisuus;
    }

    public String getTiedon_luotettavuus() {
        return tiedon_luotettavuus;
    }

    public void setTiedon_luotettavuus(String tiedon_luotettavuus) {
        this.tiedon_luotettavuus = tiedon_luotettavuus;
    }

    public Integer getAmount_total() {
        return amount_total;
    }

    public void setAmount_total(Integer amount_total) {
        this.amount_total = amount_total;
    }

}
