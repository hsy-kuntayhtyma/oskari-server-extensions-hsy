package hsy.seutumaisa.service;

import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import fi.nls.oskari.annotation.Oskari;
import fi.nls.oskari.db.DatasourceHelper;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.mybatis.MyBatisHelper;
import fi.nls.oskari.service.ServiceRuntimeException;
import hsy.seutumaisa.domain.LandMassArea;
import hsy.seutumaisa.domain.LandMassData;

@Oskari
public class LandMassAreaServiceMybatisImpl extends LandMassAreaService {
    
    private static final Logger LOG = LogFactory.getLogger(LandMassAreaServiceMybatisImpl.class);

    private SqlSessionFactory factory = null;

    public LandMassAreaServiceMybatisImpl() {
        final DatasourceHelper helper = DatasourceHelper.getInstance();
        final String name = helper.getOskariDataSourceName("seutumaisa");
        DataSource dataSource = helper.getDataSource(name);
        if (dataSource == null) {
            dataSource = helper.createDataSource(name);
        }
        if(dataSource != null) {
            factory = initializeMyBatis(dataSource);
        }
        else {
            LOG.error("Couldn't get datasource for maamassa");
        }
    }

    private SqlSessionFactory initializeMyBatis(final DataSource dataSource) {
        final Configuration configuration = MyBatisHelper.getConfig(dataSource);
        MyBatisHelper.addAliases(configuration, LandMassArea.class, LandMassData.class);
        MyBatisHelper.addMappers(configuration, LandMassAreaMapper.class);
        return new SqlSessionFactoryBuilder().build(configuration);
    }

    @Override
    public List<LandMassArea> getByCoordinate(double lon, double lat) {
        try (final SqlSession session = factory.openSession()) {
            final LandMassAreaMapper mapper = session.getMapper(LandMassAreaMapper.class);
            return mapper.getByCoordinate(lon, lat);
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to get LandMassAreas by coordinate", e);
        }
    }

    @Override
    public long save(final LandMassArea area) {
        try (final SqlSession session = factory.openSession()) {
            final LandMassAreaMapper mapper = session.getMapper(LandMassAreaMapper.class);
            long id = mapper.insert(area);
            session.commit();
            return id;
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to save announcements", e);
        }
    }

    @Override
    public void update(final LandMassArea area) {
        try (final SqlSession session = factory.openSession()) {
            final LandMassAreaMapper mapper = session.getMapper(LandMassAreaMapper.class);
            mapper.update(area);
            session.commit();
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to update announcements", e);
        }
    }

    @Override
    public void delete(long id) {
        try (final SqlSession session = factory.openSession()) {
            final LandMassAreaMapper mapper = session.getMapper(LandMassAreaMapper.class);
            mapper.delete(id);
            session.commit();
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to delete announcements", e);
        }
    }

}
