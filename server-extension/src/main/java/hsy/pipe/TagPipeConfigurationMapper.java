package hsy.pipe;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;
import org.json.JSONObject;

import fi.nls.oskari.mybatis.JSONObjectMybatisTypeHandler;

public interface TagPipeConfigurationMapper {

    @Results(id = "TagPipeResult", value = {
            @Result(property="tagId", column="tag_id"),
            @Result(property="tagType", column="tag_type"),
            @Result(property="tagAddress", column="tag_address"),
            @Result(property="tagPipeSize", column="tag_pipe_size"),
            @Result(property="tagLowPressureLevel", column="tag_low_pressure_level"),
            @Result(property="tagMaxPressureLevel", column="tag_max_pressure_level"),
            @Result(property="tagMaxWaterTake", column="tag_max_water_take"),
            @Result(property="tagMinPressureLevel", column="tag_min_pressure_level"),
            @Result(property="tagBottomHeight", column="tag_bottom_height"),
            @Result(property="tagLowTagHeight", column="tag_low_tag_height"),
            @Result(property="tagBarrageHeight", column="tag_barrage_height"),
            @Result(property="tagGroundHeight", column="tag_ground_height"),
            @Result(property="tagOtherIssue", column="tag_other_issue"),
            @Result(property="tagGeoJson", column="tag_geojson", jdbcType = JdbcType.VARCHAR, javaType = JSONObject.class, typeHandler = JSONObjectMybatisTypeHandler.class),
            @Result(property="tagMunicipality", column="tag_municipality"),
            @Result(property="tagNeighborhood", column="tag_neighborhood"),
            @Result(property="tagBlock", column="tag_block"),
            @Result(property="tagPlot", column="tag_plot"),
    })
    @Select("SELECT "
            + "tag_id,"
            + "tag_type,"
            + "tag_address,"
            + "tag_pipe_size,"
            + "tag_low_pressure_level,"
            + "tag_max_pressure_level,"
            + "tag_max_water_take,"
            + "tag_min_pressure_level,"
            + "tag_bottom_height,"
            + "tag_low_tag_height,"
            + "tag_barrage_height,"
            + "tag_ground_height,"
            + "tag_other_issue,"
            + "tag_geojson,"
            + "tag_municipality,"
            + "tag_neighborhood,"
            + "tag_block,"
            + "tag_plot"
            + " FROM oskari_tagpipes")
    List<TagPipeConfiguration> findAll();

    @Insert("INSERT INTO oskari_tagpipes ("
            + "tag_type,"
            + "tag_address,"
            + "tag_pipe_size,"
            + "tag_low_pressure_level,"
            + "tag_max_pressure_level,"
            + "tag_max_water_take,"
            + "tag_min_pressure_level,"
            + "tag_bottom_height,"
            + "tag_low_tag_height,"
            + "tag_barrage_height,"
            + "tag_ground_height,"
            + "tag_other_issue,"
            + "tag_geojson,"
            + "tag_municipality,"
            + "tag_neighborhood,"
            + "tag_block,"
            + "tag_plot"
            + ") VALUES ("
            + "${tagType},"
            + "${tagAddress},"
            + "${tagPipeSize},"
            + "${tagLowPressureLevel},"
            + "${tagMaxPressureLevel},"
            + "${tagMaxWaterTake},"
            + "${tagMinPressureLevel},"
            + "${tagBottomHeight},"
            + "${tagLowTagHeight},"
            + "${tagBarrageHeight},"
            + "${tagGroundHeight},"
            + "${tagOtherIssue},"
            + "${tagGeoJson},"
            + "${tagMunicipality},"
            + "${tagNeighborhood},"
            + "${tagBlock},"
            + "${tagPlot}"
            + ")")
    @Options(useGeneratedKeys=true, keyColumn="tag_id", keyProperty="tagId")
    int insert(TagPipeConfiguration tagpipe);

    @Update("UPDATE oskari_tagpipes SET "
            + "tag_type = ${tagType},"
            + "tag_address = ${tagAddress},"
            + "tag_pipe_size = ${tagPipeSize},"
            + "tag_low_pressure_level = ${tagLowPressureLevel},"
            + "tag_max_pressure_level = ${tagMaxPressureLevel},"
            + "tag_max_water_take = ${tagMaxWaterTake},"
            + "tag_min_pressure_level = ${tagMinPressureLevel},"
            + "tag_bottom_height = ${tagBottomHeight},"
            + "tag_low_tag_height = ${tagLowTagHeight},"
            + "tag_barrage_height = ${tagBarrageHeight},"
            + "tag_ground_height = ${tagGroundHeight},"
            + "tag_other_issue = ${tagOtherIssue},"
            + "tag_geojson = ${tagGeoJson},"
            + "tag_municipality = ${tagMunicipality},"
            + "tag_neighborhood = ${tagNeighborhood},"
            + "tag_block = ${tagBlock},"
            + "tag_plot = ${tagPlot}"
            + " WHERE tag_id = ${tagId}")
    void update(TagPipeConfiguration tagpipe);

    @Delete("DELETE FROM oskari_tagpipes WHERE tag_id=#{id}")
    void delete(@Param("id") int tagPipeId);

}
