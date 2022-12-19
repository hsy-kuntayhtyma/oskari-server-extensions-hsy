package hsy.pipe;

import java.util.List;

import fi.nls.oskari.service.OskariComponent;

public abstract class TagPipeConfigurationService extends OskariComponent {

    public abstract List<TagPipeConfiguration> findTagPipes();
    public abstract TagPipeConfiguration findTagPipeById(final int tagPipeId);
    public abstract void delete(final int tagPipeId);
    public abstract int insert(final TagPipeConfiguration tagpipe);
    public abstract void update(final TagPipeConfiguration tagpipe);
}
