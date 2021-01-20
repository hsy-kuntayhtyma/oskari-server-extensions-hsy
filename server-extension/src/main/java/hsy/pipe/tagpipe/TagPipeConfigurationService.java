package hsy.pipe.tagpipe;

import java.util.List;

import hsy.pipe.domain.TagPipeConfiguration;
import fi.nls.oskari.service.db.BaseService;

public interface TagPipeConfigurationService  extends BaseService<TagPipeConfiguration>{
    public List<TagPipeConfiguration> findTagPipes();
    public TagPipeConfiguration findTagPipeById(final int tagPipeId);
    public void delete(final int tagPipeId);
    public int insert(final TagPipeConfiguration tagpipe);
    public void update(final TagPipeConfiguration tagpipe);
}
