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
import hsy.seutumaisa.domain.Person;

@Oskari
public class LandMassServiceMybatisImpl extends LandMassService {
    
    private static final Logger LOG = LogFactory.getLogger(LandMassServiceMybatisImpl.class);

    private SqlSessionFactory factory = null;

    public LandMassServiceMybatisImpl() {
        final DatasourceHelper helper = DatasourceHelper.getInstance();
        final DataSource dataSource = helper.createDataSource("seutumaisa");
        if (dataSource != null) {
            factory = initializeMyBatis(dataSource);
        } else {
            LOG.error("Couldn't get datasource for maamassa");
        }
    }

    private SqlSessionFactory initializeMyBatis(final DataSource dataSource) {
        final Configuration configuration = MyBatisHelper.getConfig(dataSource);
        MyBatisHelper.addAliases(configuration, LandMassArea.class, LandMassData.class);
        MyBatisHelper.addMappers(configuration, LandMassMapper.class);
        return new SqlSessionFactoryBuilder().build(configuration);
    }

    @Override
    public List<LandMassArea> getAreasByCoordinate(double lon, double lat) {
        try (final SqlSession session = factory.openSession()) {
            return session.getMapper(LandMassMapper.class).getAreasByCoordinate(lon, lat);
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to get LandMassAreas by coordinate", e);
        }
    }
    
    @Override
    public List<LandMassData> getDataByAreaId(long areaId) {
        try (final SqlSession session = factory.openSession()) {
            return session.getMapper(LandMassMapper.class).getDataByAreaId(areaId);
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to get LandMassData by areaId", e);
        }
    }

    @Override
    public Person getPersonById(long personId) {
        try (final SqlSession session = factory.openSession()) {
            return session.getMapper(LandMassMapper.class).getPersonById(personId);
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to get LandMassArea by id", e);
        }
    }

    @Override
    public LandMassArea getAreaById(long id) {
        try (final SqlSession session = factory.openSession()) {
            return session.getMapper(LandMassMapper.class).getAreaById(id);
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to get LandMassArea by id", e);
        }
    }

    @Override
    public long save(final LandMassArea area) {
        try (final SqlSession session = factory.openSession()) {
            final LandMassMapper mapper = session.getMapper(LandMassMapper.class);
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
            final LandMassMapper mapper = session.getMapper(LandMassMapper.class);
            mapper.update(area);
            session.commit();
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to save announcements", e);
        }
    }

    @Override
    public void delete(long id) {
        try (final SqlSession session = factory.openSession()) {
            final LandMassMapper mapper = session.getMapper(LandMassMapper.class);
            mapper.delete(id);
            session.commit();
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to delete announcements", e);
        }
    }

}
