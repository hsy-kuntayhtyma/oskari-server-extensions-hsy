package hsy.seutumaisa.service;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedJdbcTypes({ JdbcType.ARRAY })
@MappedTypes({ int[].class })
public class IntArrayTypeHandler extends BaseTypeHandler<int[]> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, int[] parameter, JdbcType jdbcType)
            throws SQLException {
        Object[] elements = Arrays.stream(parameter)
                .mapToObj(Integer::valueOf)
                .toArray();
        Array array = ps.getConnection().createArrayOf("INTEGER", elements);
        try {
            ps.setArray(i, array);
        } finally {
            array.free();
        }
    }

    @Override
    public int[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return extractArray(rs.getArray(columnName));
    }

    @Override
    public int[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return extractArray(rs.getArray(columnIndex));
    }

    @Override
    public int[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return extractArray(cs.getArray(columnIndex));
    }
    
    private int[] extractArray(Array array) throws SQLException {
        if (array == null) {
            return null;
        }
        Object javaArray = array.getArray();
        array.free();
        return Arrays.stream((Integer[])javaArray)
            .filter(Objects::nonNull)
            .mapToInt(Integer::intValue)
            .toArray();
    }
    
}