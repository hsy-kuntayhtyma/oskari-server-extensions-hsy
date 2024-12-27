package hsy.seutumaisa.service;

import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import fi.nls.oskari.annotation.Oskari;
import fi.nls.oskari.db.DatasourceHelper;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.mybatis.JSONObjectMybatisTypeHandler;
import fi.nls.oskari.service.ServiceRuntimeException;
import hsy.seutumaisa.domain.LandMassArea;

@Oskari
public class LandMassAreaServiceMybatisImpl extends LandMassAreaService {
    
    private static final Logger LOG = LogFactory.getLogger(LandMassAreaServiceMybatisImpl.class);

    private SqlSessionFactory factory = null;

    public LandMassAreaServiceMybatisImpl() {
        final DatasourceHelper helper = DatasourceHelper.getInstance();
        DataSource dataSource = helper.getDataSource();
        if (dataSource == null) {
            dataSource = helper.createDataSource();
        }
        if (dataSource == null) {
            LOG.error("Couldn't get datasource for LandMassArea service");
        }
        factory = initializeMyBatis(dataSource);
    }

    private SqlSessionFactory initializeMyBatis(final DataSource dataSource) {
        final TransactionFactory transactionFactory = new JdbcTransactionFactory();
        final Environment environment = new Environment("development", transactionFactory, dataSource);

        final Configuration configuration = new Configuration(environment);
        configuration.getTypeAliasRegistry().registerAlias(LandMassAreaService.class);
        configuration.setLazyLoadingEnabled(true);
        configuration.getTypeHandlerRegistry().register(JSONObjectMybatisTypeHandler.class);
        configuration.addMapper(LandMassAreaMapper.class);

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
