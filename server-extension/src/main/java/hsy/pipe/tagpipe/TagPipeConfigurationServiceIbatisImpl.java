package hsy.pipe.tagpipe;

import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.ibatis.sqlmap.client.SqlMapSession;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.service.db.BaseIbatisService;
import hsy.pipe.domain.TagPipeConfiguration;

public class TagPipeConfigurationServiceIbatisImpl extends BaseIbatisService<TagPipeConfiguration> implements TagPipeConfigurationService{
	private final static Logger log = LogFactory.getLogger(TagPipeConfigurationServiceIbatisImpl.class);
	private SqlMapClient client = null;
	
    @Override
    protected String getNameSpace() {
        return "TagPipeConfiguration";
    }
    
    public List<TagPipeConfiguration> findTagPipes() {
        List<TagPipeConfiguration> tagpipes = queryForList(getNameSpace() + ".findTagPipes");
        return tagpipes;
    }
    
    public TagPipeConfiguration findTagPipeById(final int tagPipeId) {
    	long tagpipe_id = Long.valueOf(tagPipeId);
        TagPipeConfiguration tagpipe = queryForObject(getNameSpace() + ".findTagPipeById", tagpipe_id);
        return tagpipe;
    }
    
    
    /*
     * The purpose of this method is to allow many SqlMapConfig.xml files in a
     * single portlet
     */
    protected String getSqlMapLocation() {
        return "META-INF/SqlMapConfig-tagpipe-configuration.xml";
    }
    
    /**
     * Returns SQLmap
     * 
     * @return
     */
    @Override
    protected SqlMapClient getSqlMapClient() {
        if (client != null) {
            return client;
        }

        Reader reader = null;
        try {
            String sqlMapLocation = getSqlMapLocation();
            reader = Resources.getResourceAsReader(sqlMapLocation);
            client = SqlMapClientBuilder.buildSqlMapClient(reader);
            return client;
        } catch (Exception e) {
        	log.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("Failed to retrieve SQL client", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    public void delete(final int tagPipeId) {
    	long tagpipe_id = Long.valueOf(tagPipeId);
        final SqlMapSession session = openSession();
        try {
            session.startTransaction();
            // remove tagpipe
            session.delete(getNameSpace() + ".delete", tagpipe_id);
            session.commitTransaction();
        } catch (Exception e) {
        	log.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            new RuntimeException("Error deleting tagpipe with id:" + Long.toString(tagpipe_id), e);
        } finally {
            endSession(session);
        }    	
    };
    
    public synchronized int insert(final TagPipeConfiguration tagpipe) {
        SqlMapClient client = null;
        try {
            client = getSqlMapClient();
            client.startTransaction();
            client.insert(getNameSpace() + ".insert", tagpipe);
            Integer id = (Integer) client.queryForObject(getNameSpace() + ".maxId");
            client.commitTransaction();
            return id;
        } catch (Exception e) {
        	log.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("Failed to insert tagpipe", e);
        } finally {
            if (client != null) {
                try {
                    client.endTransaction();
                } catch (SQLException ignored) { }
            }
        }
    }
    
    public void update(final TagPipeConfiguration tagpipe) {
    	try {
            getSqlMapClient().update(getNameSpace() + ".update", tagpipe);
        } catch (Exception e) {
        	log.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("Failed to update tagpipe", e);
        }
    };
}
