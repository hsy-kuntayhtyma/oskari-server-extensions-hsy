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

import hsy.seutumaisa.domain.LandmassProject;

public interface LandmassProjectMapper {

    @Results(id = "LandmassProjectResult", value = {
            @Result(property="id", column="id", id=true),
            @Result(property="nimi", column="nimi"),
            @Result(property="kunta", column="kunta")
    })
    @Select("SELECT id, nimi, kunta FROM hankealue WHERE id = #{id}")
    LandmassProject getById(@Param("id") long id);

    @ResultMap("LandmassProjectResult")
    @Select("SELECT id, nimi, kunta FROM hankealue")
    List<LandmassProject> getAll();

    @Select("INSERT INTO hankealue (nimi, kunta) VALUES (#{nimi}, #{kunta}) RETURNING id")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    long insert(LandmassProject project);

    @Update("UPDATE hankealue SET nimi = #{nimi}, kunta = #{kunta} WHERE id = #{id}")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    boolean update(LandmassProject project);

    @Delete("DELETE FROM hankealue WHERE id = #{id}")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    boolean delete(@Param("id") long id);

}