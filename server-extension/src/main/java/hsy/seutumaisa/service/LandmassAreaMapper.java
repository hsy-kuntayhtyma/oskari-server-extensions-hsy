package hsy.seutumaisa.service;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import hsy.seutumaisa.domain.LandmassArea;
import hsy.seutumaisa.domain.LandmassData;
import hsy.seutumaisa.domain.Person;

public interface LandmassAreaMapper {

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
            @Result(property="loppu_pvm", column="loppu_pvm"),
            @Result(property="hankealue_id", column="hankealue_id"),
            @Result(property="createdByUserId", column="created_by_oskari_user_id")
    })
    @Select("SELECT id, ST_AsGeoJSON(geom, 3, 0) AS geom, nimi, osoite, kunta, kohdetyyppi, vaihe, omistaja_id, alku_pvm, loppu_pvm, hankealue_id, created_by_oskari_user_id "
            + "FROM maamassakohde "
            + "WHERE id = #{id}")
    LandmassArea getAreaById(@Param("id") int id);

    @ResultMap("LandMassAreaResult")
    @Select("SELECT id, ST_AsGeoJSON(geom, 3, 0) AS geom, nimi, osoite, kunta, kohdetyyppi, vaihe, omistaja_id, alku_pvm, loppu_pvm, hankealue_id, created_by_oskari_user_id "
            + "FROM maamassakohde "
            + "WHERE ST_DWithin(geom, ST_SetSRID(ST_MakePoint(#{lon}, #{lat}), 3879), 10) "
            + "ORDER BY ST_Distance(geom, ST_SetSRID(ST_MakePoint(#{lon}, #{lat}), 3879))")
    List<LandmassArea> getAreasByCoordinate(@Param("lon") double lon, @Param("lat") double lat);

    @Select("INSERT INTO maamassakohde (geom, nimi, osoite, kunta, kohdetyyppi, vaihe, omistaja_id, alku_pvm, loppu_pvm, hankealue_id, created_by_oskari_user_id) VALUES"
            + " (ST_SetSRID(ST_GeomFromGeoJSON(#{geom}), 3879), #{nimi}, #{osoite}, #{kunta}, #{kohdetyyppi}::kohdetyyppi, #{vaihe}::vaihe, #{omistaja_id}, #{alku_pvm}, #{loppu_pvm}, #{hankealue_id}, #{createdByUserId})"
            + " RETURNING id")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    int insertArea(final LandmassArea area);

    @Update("UPDATE maamassakohde SET "
            + "geom = ST_SetSRID(ST_GeomFromGeoJSON(#{geom}), 3879),"
            + "nimi = #{nimi},"
            + "osoite = #{osoite},"
            + "kunta = #{kunta},"
            + "kohdetyyppi = #{kohdetyyppi}::kohdetyyppi,"
            + "vaihe = #{vaihe}::vaihe,"
            + "omistaja_id = #{omistaja_id},"
            + "alku_pvm = #{alku_pvm},"
            + "loppu_pvm = #{loppu_pvm},"
            + "hankealue_id = #{hankealue_id}"
            + " WHERE id = #{id}")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    boolean updateArea(final LandmassArea area);

    @Delete("DELETE FROM maamassakohde WHERE id = #{id}")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    boolean deleteArea(@Param("id") final int id);

    @Results(id = "LandMassDataResult", value = {
            @Result(property="id", column="id", id=true),
            @Result(property="maamassakohde_id", column="maamassakohde_id"),
            @Result(property="maamassaryhma", column="maamassaryhma"),
            @Result(property="maamassalaji", column="maamassalaji"),
            @Result(property="kelpoisuusluokkaryhma", column="kelpoisuusluokkaryhma"),
            @Result(property="kelpoisuusluokka", column="kelpoisuusluokka"),
            @Result(property="maamassatila", column="maamassatila"),
            @Result(property="tiedontuottaja", column="tiedontuottaja"),
            @Result(property="planned_begin_date", column="planned_begin_date"),
            @Result(property="planned_end_date", column="planned_end_date"),
            @Result(property="amount_remaining", column="amount_remaining"),
            @Result(property="amount_unit", column="amount_unit"),
            @Result(property="vertical_position", column="vertical_position"),
            @Result(property="lisatieto", column="lisatieto"),
            @Result(property="liitteet", column="liitteet"),
            @Result(property="varattu", column="varattu"),
            @Result(property="muokattu", column="muokattu"),
            @Result(property="luotu", column="luotu"),
            @Result(property="realized_begin_date", column="realized_begin_date"),
            @Result(property="realized_end_date", column="realized_end_date"),
            @Result(property="pilaantuneisuus", column="pilaantuneisuus"),
            @Result(property="tiedon_luotettavuus", column="tiedon_luotettavuus"),
            @Result(property="amount_total", column="amount_total")
    })
    @Select("SELECT id,"
            + "maamassakohde_id,"
            + "maamassaryhma,"
            + "maamassalaji,"
            + "kelpoisuusluokkaryhma,"
            + "kelpoisuusluokka,"
            + "maamassatila,"
            + "tiedontuottaja,"
            + "planned_begin_date,"
            + "planned_end_date,"
            + "amount_remaining,"
            + "amount_unit,"
            + "vertical_position,"
            + "lisatieto,"
            + "liitteet,"
            + "varattu,"
            + "muokattu,"
            + "luotu,"
            + "realized_begin_date,"
            + "realized_end_date,"
            + "pilaantuneisuus,"
            + "tiedon_luotettavuus,"
            + "amount_total "
            + "FROM maamassatieto "
            + "WHERE maamassakohde_id = #{areaId}")
    List<LandmassData> getDataByAreaId(@Param("areaId") int areaId);

    @Select("INSERT INTO maamassatieto ("
            + "maamassakohde_id,"
            + "maamassaryhma,"
            + "maamassalaji,"
            + "kelpoisuusluokkaryhma,"
            + "kelpoisuusluokka,"
            + "maamassatila,"
            + "planned_begin_date,"
            + "planned_end_date,"
            + "amount_remaining,"
            + "amount_unit,"
            + "vertical_position,"
            + "lisatieto,"
            + "liitteet,"
            + "varattu,"
            + "realized_begin_date,"
            + "realized_end_date,"
            + "pilaantuneisuus,"
            + "tiedon_luotettavuus,"
            + "amount_total,"
            + "tiedontuottaja"
            + ") VALUES ("
            + "#{maamassakohde_id},"
            + "#{maamassaryhma}::maamassan_ryhma,"
            + "#{maamassalaji}::maamassan_laji,"
            + "#{kelpoisuusluokkaryhma}::kelpoisuusluokka_ryhma,"
            + "#{kelpoisuusluokka}::kelpoisuusluokka,"
            + "#{maamassatila}::maamassan_tila,"
            + "#{planned_begin_date},"
            + "#{planned_end_date},"
            + "#{amount_remaining},"
            + "#{amount_unit},"
            + "#{vertical_position},"
            + "#{lisatieto},"
            + "#{liitteet},"
            + "#{varattu},"
            + "#{realized_begin_date},"
            + "#{realized_end_date},"
            + "#{pilaantuneisuus}::pilaantuneisuus,"
            + "#{tiedon_luotettavuus}::tiedon_luotettavuus,"
            + "#{amount_total},"
            + "#{tiedontuottaja}"
            + ") RETURNING id")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    int insertData(final LandmassData data);

    @Update("UPDATE maamassatieto SET "
            + "maamassakohde_id = #{maamassakohde_id},"
            + "maamassaryhma = #{maamassaryhma}::maamassan_ryhma,"
            + "maamassalaji = #{maamassalaji}::maamassan_laji,"
            + "kelpoisuusluokkaryhma = #{kelpoisuusluokkaryhma}::kelpoisuusluokka_ryhma,"
            + "kelpoisuusluokka = #{kelpoisuusluokka}::kelpoisuusluokka,"
            + "maamassatila = #{maamassatila}::maamassan_tila,"
            + "planned_begin_date = #{planned_begin_date},"
            + "planned_end_date = #{planned_end_date},"
            + "amount_remaining = #{amount_remaining},"
            + "amount_unit = #{amount_unit},"
            + "vertical_position = #{vertical_position},"
            + "lisatieto = #{lisatieto},"
            + "liitteet = #{liitteet},"
            + "varattu = #{varattu},"
            + "realized_begin_date = #{realized_begin_date},"
            + "realized_end_date = #{realized_end_date},"
            + "pilaantuneisuus = #{pilaantuneisuus}::pilaantuneisuus,"
            + "tiedon_luotettavuus = #{tiedon_luotettavuus}::tiedon_luotettavuus,"
            + "amount_total = #{amount_total},"
            + "tiedontuottaja = #{tiedontuottaja}"
            + " WHERE id = #{id}")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    boolean updateData(final LandmassData data);

    @Results(id = "PersonResult", value = {
            @Result(property="id", column="id", id=true),
            @Result(property="nimi", column="nimi"),
            @Result(property="email", column="email"),
            @Result(property="puhelin", column="puhelin"),
            @Result(property="organisaatio", column="organisaatio")
    })
    @Select("SELECT id, nimi, email, puhelin, organisaatio FROM henkilo WHERE id = #{id}")
    Person getPersonById(@Param("id") int id);


    @ResultMap("PersonResult")
    @Select("SELECT id, nimi, email, puhelin, organisaatio FROM henkilo WHERE email = #{email}")
    Person getPersonByEmail(@Param("email") String email);

    @Select("INSERT INTO henkilo (nimi, email, puhelin, organisaatio) VALUES"
            + " (#{nimi}, #{email}, #{puhelin}, #{organisaatio})"
            + " RETURNING id")
    int insertPerson(Person person);

    @Update("UPDATE henkilo SET "
            + "nimi = #{nimi},"
            + "email = #{email},"
            + "puhelin = #{puhelin},"
            + "organisaatio = #{organisaatio}"
            + " WHERE id = #{id}")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    boolean updatePerson(Person person);

}