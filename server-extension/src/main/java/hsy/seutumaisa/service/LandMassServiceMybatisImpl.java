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
        try (final SqlSession session = factory.openSession(false)) {
            LandMassMapper mapper = session.getMapper(LandMassMapper.class);
            List<LandMassArea> areas = mapper.getAreasByCoordinate(lon, lat);
            for (LandMassArea area : areas) {
                includeOwnerAndData(mapper, area);
            }
            return areas;
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to get LandMassAreas by coordinate", e);
        }
    }

    @Override
    public LandMassArea getAreaById(long id) {
        try (final SqlSession session = factory.openSession(false)) {
            LandMassMapper mapper = session.getMapper(LandMassMapper.class);
            LandMassArea area = mapper.getAreaById(id);
            includeOwnerAndData(mapper, area);
            return area;
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to get LandMassArea by id", e);
        }
    }

    @Override
    public void save(final LandMassArea area) {
        try (final SqlSession session = factory.openSession(false)) {
            final LandMassMapper mapper = session.getMapper(LandMassMapper.class);

            upsertPerson(area, mapper);

            long id = mapper.insertArea(area);
            area.setId(id);

            for (LandMassData data : area.getData()) {
                mapper.insertData(data);
            }

            session.commit();
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to landmass area", e);
        }
    }

    @Override
    public void update(final LandMassArea area) {
        try (final SqlSession session = factory.openSession(false)) {
            final LandMassMapper mapper = session.getMapper(LandMassMapper.class);

            upsertPerson(area, mapper);

            mapper.updateArea(area);

            for (LandMassData data : area.getData()) {
                if (data.getId() == null) {
                    mapper.insertData(data);
                } else {
                    mapper.updateData(data);
                }
            }

            session.commit();
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to update landmass area", e);
        }
    }
    
    @Override
    public void delete(long id) {
        try (final SqlSession session = factory.openSession(false)) {
            final LandMassMapper mapper = session.getMapper(LandMassMapper.class);
            mapper.deleteArea(id);
            session.commit();
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to delete landmass area", e);
        }
    }

    private static void includeOwnerAndData(LandMassMapper mapper, LandMassArea area) {
        if (area.getOmistaja_id() != null) {
            Person person = mapper.getPersonById(area.getOmistaja_id());
            if (person != null) {
                area.setHenkilo_nimi(person.getNimi());
                area.setHenkilo_puhelin(person.getPuhelin());
                area.setHenkilo_email(person.getEmail());
                area.setHenkilo_organisaatio(person.getOrganisaatio());
            }
        }
        if (area.getId() != null) {
            area.setData(mapper.getDataByAreaId(area.getId()));
        }
    }

    private static void upsertPerson(LandMassArea area, LandMassMapper mapper) {
        Long personId = findPersonId(area.getOmistaja_id(), area.getHenkilo_email(), mapper); 

        Person person = new Person();
        person.setId(personId);
        person.setNimi(area.getHenkilo_nimi());
        person.setEmail(area.getHenkilo_email());
        person.setPuhelin(area.getHenkilo_puhelin());
        person.setOrganisaatio(area.getHenkilo_organisaatio());
        
        if (personId == null) {
            personId = mapper.insertPerson(person);
        } else {
            mapper.updatePerson(person);
        }
        area.setOmistaja_id(personId);
    }

    private static Long findPersonId(Long omistajaId, String email, LandMassMapper mapper) {
        if (omistajaId != null) {
            return omistajaId;
        }
        Person byEmail = mapper.getPersonByEmail(email);
        if (byEmail != null) {
            return byEmail.getId();
        }
        return null;
    }

}
