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
import hsy.seutumaisa.domain.LandmassProject;

@Oskari
public class LandmassProjectServiceMybatisImpl extends LandmassProjectService {

    private static final Logger LOG = LogFactory.getLogger(LandmassProjectServiceMybatisImpl.class);

    private SqlSessionFactory factory = null;

    public LandmassProjectServiceMybatisImpl() {
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
        MyBatisHelper.addAliases(configuration, LandmassProject.class);
        MyBatisHelper.addMappers(configuration, LandmassProjectMapper.class);
        return new SqlSessionFactoryBuilder().build(configuration);
    }

    @Override
    public LandmassProject getById(long id) {
        try (final SqlSession session = factory.openSession(false)) {
            return session.getMapper(LandmassProjectMapper.class).getById(id);
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to get landmass project by id", e);
        }
    }

    @Override
    public List<LandmassProject> getAll() {
        try (final SqlSession session = factory.openSession(false)) {
            return session.getMapper(LandmassProjectMapper.class).getAll();
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to get landmass projects by kunta", e);
        }
    }

    @Override
    public void save(final LandmassProject project) {
        try (final SqlSession session = factory.openSession(false)) {
            long id = session.getMapper(LandmassProjectMapper.class).insert(project);
            session.commit();
            project.setId(id);
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to insert landmass project", e);
        }
    }

    @Override
    public void update(final LandmassProject project) {
        try (final SqlSession session = factory.openSession(false)) {
            session.getMapper(LandmassProjectMapper.class).update(project);
            session.commit();
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to update landmass project", e);
        }
    }

    @Override
    public void delete(long id) {
        try (final SqlSession session = factory.openSession(false)) {
            session.getMapper(LandmassProjectMapper.class).delete(id);
            session.commit();
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to delete landmass project", e);
        }
    }

}
