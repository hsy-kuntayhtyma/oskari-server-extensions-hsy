package hsy.seutumaisa.service;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import hsy.seutumaisa.domain.LandMassArea;

public interface LandMassAreaMapper {

    @Results(id = "LandMassAreaResult", value = {
            @Result(property="id", column="id", id=true),
            @Result(property="nimi", column="nimi"),
            @Result(property="osoite", column="osoite"),
            @Result(property="geom", column="geom"),
            @Result(property="kohdetyyppi", column="kohdetyyppi"),
            @Result(property="vaihe", column="vaihe"),
            @Result(property="maamassatila", column="maamassatila"),
            @Result(property="omistaja_id", column="omistaja_id"),
            @Result(property="alku_pvm", column="alku_pvm"),
            @Result(property="loppu_pvm", column="loppu_pvm"),
            @Result(property="lisatieto", column="lisatieto"),
            @Result(property="kunta", column="kunta"),
            @Result(property="status", column="status")
    })
    @Select("SELECT id, nimi, osoite, ST_AsEWKT(geom) AS geom, kohdetyyppi, vaihe, maamassatila, omistaja_id, alku_pvm, loppu_pvm, lisatieto, kunta, status "
            + "FROM maamassakohde "
            + "WHERE ST_DWithin(geom, ST_SetSRID(ST_MakePoint(#{lon}, #{lat}), 3879), 10)")
    List<LandMassArea> getByCoordinate(double lon, double lat);
    
    @Insert("INSERT INTO maamassakohde (nimi, osoite, geom, kohdetyyppi, vaihe, maamassatila, omistaja_id, alku_pvm, loppu_pvm, lisatieto, kunta, status) VALUES"
            + " (#{nimi}, #{osoite}, ST_GeomFromEWKT(#{geom}), #{kohdetyyppi}, #{vaihe}, #{maamassatila}, #{omistaja_id}, #{alku_pvm}, #{loppu_pvm}, #{lisatieto}, #{kunta}, #{status})"
            + " RETURNING id")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    long insert(final LandMassArea area);

    @Update("UPDATE maamassakohde SET "
            + "nimi = #{nimi},"
            + "osoite = #{osoite},"
            + "geom = ST_GeomFromEWKT(#{geom}),"
            + "kohdetyyppi = #{kohdetyyppi},"
            + "vaihe = #{vaihe},"
            + "maamassatila = #{maamassatila},"
            + "omistaja_id = #{omistaja_id},"
            + "alku_pvm = #{alku_pvm},"
            + "loppu_pvm = #{loppu_pvm},"
            + "lisatieto = #{lisatieto},"
            + "kunta = #{kunta},"
            + "status = #{status}"
            + " WHERE id = #{id}")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    boolean update(final LandMassArea area);
    
    @Delete("DELETE FROM maamassakohde WHERE id = #{id}")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    boolean delete(@Param("id") final long id);

}