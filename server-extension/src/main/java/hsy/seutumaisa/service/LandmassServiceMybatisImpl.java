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
import hsy.seutumaisa.domain.LandmassArea;
import hsy.seutumaisa.domain.LandmassData;
import hsy.seutumaisa.domain.Person;

@Oskari
public class LandmassServiceMybatisImpl extends LandmassService {

    private static final Logger LOG = LogFactory.getLogger(LandmassServiceMybatisImpl.class);

    private SqlSessionFactory factory = null;

    public LandmassServiceMybatisImpl() {
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
        MyBatisHelper.addAliases(configuration, LandmassArea.class, LandmassData.class);
        MyBatisHelper.addMappers(configuration, LandmassMapper.class);
        return new SqlSessionFactoryBuilder().build(configuration);
    }

    @Override
    public List<LandmassArea> getAreasByCoordinate(double lon, double lat) {
        try (final SqlSession session = factory.openSession(false)) {
            LandmassMapper mapper = session.getMapper(LandmassMapper.class);
            List<LandmassArea> areas = mapper.getAreasByCoordinate(lon, lat);
            for (LandmassArea area : areas) {
                includeOwnerAndData(mapper, area);
            }
            return areas;
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to get LandMassAreas by coordinate", e);
        }
    }

    @Override
    public LandmassArea getAreaById(long id) {
        try (final SqlSession session = factory.openSession(false)) {
            LandmassMapper mapper = session.getMapper(LandmassMapper.class);
            LandmassArea area = mapper.getAreaById(id);
            includeOwnerAndData(mapper, area);
            return area;
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to get LandMassArea by id", e);
        }
    }

    @Override
    public void save(final LandmassArea area) {
        try (final SqlSession session = factory.openSession(false)) {
            final LandmassMapper mapper = session.getMapper(LandmassMapper.class);

            upsertPerson(area, mapper);

            long areaId = mapper.insertArea(area);
            area.setId(areaId);

            for (LandmassData data : area.getData()) {
                data.setMaamassakohde_id(areaId);
                long dataId = mapper.insertData(data);
                data.setId(dataId);
            }

            session.commit();
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to landmass area", e);
        }
    }

    @Override
    public void update(final LandmassArea area) {
        try (final SqlSession session = factory.openSession(false)) {
            final LandmassMapper mapper = session.getMapper(LandmassMapper.class);

            upsertPerson(area, mapper);

            mapper.updateArea(area);

            for (LandmassData data : area.getData()) {
                data.setMaamassakohde_id(area.getId());
                if (data.getId() == null) {
                    long dataId = mapper.insertData(data);
                    data.setId(dataId);
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
            final LandmassMapper mapper = session.getMapper(LandmassMapper.class);
            mapper.deleteArea(id);
            session.commit();
        } catch (Exception e) {
            throw new ServiceRuntimeException("Failed to delete landmass area", e);
        }
    }

    private static void includeOwnerAndData(LandmassMapper mapper, LandmassArea area) {
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

    private static void upsertPerson(LandmassArea area, LandmassMapper mapper) {
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

    private static Long findPersonId(Long omistajaId, String email, LandmassMapper mapper) {
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
