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
            @Result(property="kunta", column="kunta"),
            @Result(property="editors", column="editors", typeHandler=hsy.seutumaisa.service.IntArrayTypeHandler.class),
            @Result(property="managers", column="managers", typeHandler=hsy.seutumaisa.service.IntArrayTypeHandler.class)
    })
    @Select("SELECT id, nimi, kunta, editors, managers FROM hankealue WHERE id = #{id}")
    LandmassProject getById(@Param("id") int id);

    @ResultMap("LandmassProjectResult")
    @Select("SELECT id, nimi, kunta, editors, managers FROM hankealue")
    List<LandmassProject> getAll();

    @Select("INSERT INTO hankealue (nimi, kunta, editors, managers) VALUES (#{nimi}, #{kunta}, #{editors, typeHandler=hsy.seutumaisa.service.IntArrayTypeHandler}, #{managers, typeHandler=hsy.seutumaisa.service.IntArrayTypeHandler}) RETURNING id")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    int insert(LandmassProject project);

    @Update("UPDATE hankealue SET nimi = #{nimi}, kunta = #{kunta}, editors = #{editors, typeHandler=hsy.seutumaisa.service.IntArrayTypeHandler}, managers = #{managers, typeHandler=hsy.seutumaisa.service.IntArrayTypeHandler} WHERE id = #{id}")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    boolean update(LandmassProject project);

    @Delete("DELETE FROM hankealue WHERE id = #{id}")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    boolean delete(@Param("id") int id);

}