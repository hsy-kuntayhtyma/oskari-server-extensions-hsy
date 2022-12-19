package hsy.pipe;

import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import fi.nls.oskari.annotation.Oskari;
import fi.nls.oskari.db.DatasourceHelper;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.mybatis.MyBatisHelper;

@Oskari
public class TagPipeConfigurationServiceMyBatisImpl extends TagPipeConfigurationService {

    private static final Logger LOG = LogFactory.getLogger(TagPipeConfigurationServiceMyBatisImpl.class);
    private static final Class<TagPipeConfigurationMapper> MAPPER = TagPipeConfigurationMapper.class;

    private final SqlSessionFactory factory;

    public TagPipeConfigurationServiceMyBatisImpl() {
        this(DatasourceHelper.getInstance().getDataSource());
    }

    public TagPipeConfigurationServiceMyBatisImpl(DataSource ds) {
        super();
        if (ds == null) {
            LOG.warn("DataSource was null, all future calls will throw NPEs!");
            factory = null;
        } else {
            factory = MyBatisHelper.initMyBatis(ds, MAPPER);
        }

    }

    public List<TagPipeConfiguration> findTagPipes() {
        try (SqlSession session = factory.openSession()) {
            return session.getMapper(MAPPER).findAll();
        }
    }

    public void delete(final int tagPipeId) {
        try (SqlSession session = factory.openSession()) {
            session.getMapper(MAPPER).delete(tagPipeId);
        }
    }

    public int insert(final TagPipeConfiguration tagpipe) {
        try (SqlSession session = factory.openSession()) {
            session.getMapper(MAPPER).insert(tagpipe);
            return tagpipe.getTagId();
        }
    }

    public void update(final TagPipeConfiguration tagpipe) {
        try (SqlSession session = factory.openSession()) {
            session.getMapper(MAPPER).update(tagpipe);
        }
    }

}
