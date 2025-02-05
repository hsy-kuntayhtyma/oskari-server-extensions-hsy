package hsy.seutumaisa.service;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import hsy.seutumaisa.domain.LandMassArea;
import hsy.seutumaisa.domain.LandMassData;
import hsy.seutumaisa.domain.Person;

public interface LandMassMapper {

    @Results(id = "LandMassAreaResult", value = {
            @Result(property="id", column="id", id=true),
            @Result(property="geom", column="geom"),
            @Result(property="nimi", column="nimi"),
            @Result(property="osoite", column="osoite"),
            @Result(property="kunta", column="kunta"),
            @Result(property="kohdetyyppi", column="kohdetyyppi"),
            @Result(property="vaihe", column="vaihe"),
            @Result(property="omistaja_id", column="omistaja_id"),
            @Result(property="alku_pvm", column="alku_pvm"),
            @Result(property="loppu_pvm", column="loppu_pvm")
    })
    @Select("SELECT id, ST_AsEWKT(geom) AS geom, nimi, osoite, kunta, kohdetyyppi, vaihe, omistaja_id, alku_pvm, loppu_pvm "
            + "FROM maamassakohde "
            + "WHERE id = #{id}")
    LandMassArea getAreaById(@Param("id") long id);

    @ResultMap("LandMassAreaResult")
    @Select("SELECT id, ST_AsEWKT(geom) AS geom, nimi, osoite, kunta, kohdetyyppi, vaihe, omistaja_id, alku_pvm, loppu_pvm "
            + "FROM maamassakohde "
            + "WHERE ST_DWithin(geom, ST_SetSRID(ST_MakePoint(#{lon}, #{lat}), 3879), 10) "
            + "ORDER BY ST_Distance(geom, ST_SetSRID(ST_MakePoint(#{lon}, #{lat}), 3879))")
    List<LandMassArea> getAreasByCoordinate(@Param("lon") double lon, @Param("lat") double lat);

    @Insert("INSERT INTO maamassakohde (geom, nimi, osoite, kunta, kohdetyyppi, vaihe, omistaja_id, alku_pvm, loppu_pvm) VALUES"
            + " (ST_GeomFromEWKT(#{geom}, #{nimi}, #{osoite}, #{kunta}, #{kohdetyyppi}, #{vaihe}, #{omistaja_id}, #{alku_pvm}, #{loppu_pvm})"
            + " RETURNING id")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    long insertArea(final LandMassArea area);

    @Update("UPDATE maamassakohde SET "
            + "geom = ST_GeomFromEWKT(#{geom}),"
            + "nimi = #{nimi},"
            + "osoite = #{osoite},"
            + "kunta = #{kunta},"
            + "kohdetyyppi = #{kohdetyyppi},"
            + "vaihe = #{vaihe},"
            + "omistaja_id = #{omistaja_id},"
            + "alku_pvm = #{alku_pvm},"
            + "loppu_pvm = #{loppu_pvm}"
            + " WHERE id = #{id}")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    boolean updateArea(final LandMassArea area);

    @Delete("DELETE FROM maamassakohde WHERE id = #{id}")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    boolean deleteArea(@Param("id") final long id);

    @Results(id = "LandMassDataResult", value = {
            @Result(property="id", column="id", id=true),
            @Result(property="maamassakohde_id", column="maamassakohde_id"),
            @Result(property="maamassaryhma", column="maamassaryhma"),
            @Result(property="maamassalaji", column="maamassalaji"),
            @Result(property="kelpoisuusluokkaryhma", column="kelpoisuusluokkaryhma"),
            @Result(property="kelpoisuusluokka", column="kelpoisuusluokka"),
            @Result(property="maamassatila", column="maamassatila"),
            @Result(property="tiedontuottaja_id", column="tiedontuottaja_id"),
            @Result(property="tiedontuottaja", column="tiedontuottaja"),
            @Result(property="planned_begin_date", column="planned_begin_date"),
            @Result(property="planned_end_date", column="planned_end_date"),
            @Result(property="amount_remaining", column="amount_remaining"),
            @Result(property="lisatieto", column="lisatieto"),
            @Result(property="liitteet", column="liitteet"),
            @Result(property="varattu", column="varattu"),
            @Result(property="muokattu", column="muokattu"),
            @Result(property="luotu", column="luotu"),
            @Result(property="realized_begin_date", column="realized_begin_date"),
            @Result(property="realized_end_date", column="realized_end_date"),
            @Result(property="pilaantuneisuus", column="pilaantuneisuus"),
            @Result(property="tiedon_luotettavuus", column="tiedon_luotettavuus"),
            @Result(property="amount_total", column="amount_total"),
            @Result(property="external_id", column="external_id"),
            @Result(property="alkupera_id", column="alkupera_id")
    })
    @Select("SELECT id,"
            + "maamassakohde_id,"
            + "maamassaryhma,"
            + "maamassalaji,"
            + "kelpoisuusluokkaryhma,"
            + "kelpoisuusluokka,"
            + "maamassatila,"
            + "tiedontuottaja_id,"
            + "tiedontuottaja,"
            + "planned_begin_date,"
            + "planned_end_date,"
            + "amount_remaining,"
            + "lisatieto,"
            + "liitteet,"
            + "varattu,"
            + "muokattu,"
            + "luotu,"
            + "realized_begin_date,"
            + "realized_end_date,"
            + "pilaantuneisuus,"
            + "tiedon_luotettavuus,"
            + "amount_total,"
            + "external_id,"
            + "alkupera_id "
            + "FROM maamassatieto "
            + "WHERE maamassakohde_id = #{areaId}")
    List<LandMassData> getDataByAreaId(@Param("areaId") long areaId);

    @Insert("INSERT INTO maamassatieto ("
            + "maamassakohde_id,"
            + "maamassaryhma,"
            + "maamassalaji,"
            + "kelpoisuusluokkaryhma,"
            + "kelpoisuusluokka,"
            + "maamassatila,"
            + "tiedontuottaja_id,"
            + "tiedontuottaja"
            + "planned_begin_date,"
            + "planned_end_date,"
            + "amount_remaining,"
            + "lisatieto,"
            + "liitteet,"
            + "varattu,"
            + "realized_begin_date,"
            + "realized_end_date,"
            + "pilaantuneisuus,"
            + "tiedon_luotettavuus,"
            + "amount_total,"
            + "kunta,"
            + "external_id,"
            + "alkupera_id"
            + ") VALUES ("
            + "#{maamassakohde_id},"
            + "#{maamassaryhma},"
            + "#{maamassalaji},"
            + "#{kelpoisuusluokkaryhma},"
            + "#{kelpoisuusluokka},"
            + "#{maamassatila},"
            + "#{tiedontuottaja_id},"
            + "#{tiedontuottaja},"
            + "#{planned_begin_date},"
            + "#{planned_end_date},"
            + "#{amount_remaining},"
            + "#{lisatieto},"
            + "#{liitteet},"
            + "#{varattu},"
            + "#{realized_begin_date},"
            + "#{realized_end_date},"
            + "#{pilaantuneisuus},"
            + "#{tiedon_luotettavuus},"
            + "#{amount_total},"
            + "#{kunta},"
            + "#{external_id},"
            + "#{alkupera_id}"
            + ") RETURNING id")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    long insertData(final LandMassData data);

    @Update("UPDATE maamassatieto SET "
            + "maamassakohde_id = #{maamassakohde_id},"
            + "maamassaryhma = #{maamassaryhma},"
            + "maamassalaji = #{maamassalaji},"
            + "kelpoisuusluokkaryhma = #{kelpoisuusluokkaryhma},"
            + "kelpoisuusluokka = #{kelpoisuusluokka},"
            + "maamassatila = #{maamassatila},"
            + "tiedontuottaja_id = #{tiedontuottaja_id},"
            + "planned_begin_date = #{planned_begin_date},"
            + "planned_end_date = #{planned_end_date},"
            + "amount_remaining = #{amount_remaining},"
            + "lisatieto = #{lisatieto},"
            + "liitteet = #{liitteet},"
            + "varattu = #{varattu},"
            + "realized_begin_date = #{realized_begin_date},"
            + "realized_end_date = #{realized_end_date},"
            + "pilaantuneisuus = #{pilaantuneisuus},"
            + "tiedon_luotettavuus = #{tiedon_luotettavuus},"
            + "amount_total = #{amount_total},"
            + "kunta = #{kunta},"
            + "external_id = #{external_id},"
            + "alkupera_id = #{alkupera_id},"
            + "tiedontuottaja = #{tiedontuottaja}"
            + " WHERE id = #{id}")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    boolean updateData(final LandMassData data);

    @Results(id = "PersonResult", value = {
            @Result(property="id", column="id", id=true),
            @Result(property="nimi", column="nimi"),
            @Result(property="email", column="email"),
            @Result(property="puhelin", column="puhelin"),
            @Result(property="organisaatio", column="organisaatio")
    })
    @Select("SELECT id, nimi, email, puhelin, organisaatio FROM henkilo WHERE id = #{id}")
    Person getPersonById(long personId);

    @Insert("INSERT INTO henkilo (nimi, email, puhelin, organisaatio) VALUES"
            + " (#{nimi}, #{email}, #{puhelin}, #{organisaatio})"
            + " RETURNING id")
    long insertPerson(Person person);

    @Update("UPDATE henkilo SET "
            + "nimi = #{nimi},"
            + "email = #{email},"
            + "puhelin = #{puhelin},"
            + "organisaatio = #{organisaatio}"
            + " WHERE id = #{id}")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    Person updatePerson(Person person);

}