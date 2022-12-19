package hsy.pipe;

import java.util.List;

public interface TagPipeConfigurationService {

    public List<TagPipeConfiguration> findTagPipes();
    public TagPipeConfiguration findTagPipeById(final int tagPipeId);
    public void delete(final int tagPipeId);
    public int insert(final TagPipeConfiguration tagpipe);
    public void update(final TagPipeConfiguration tagpipe);
}
