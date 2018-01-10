/*
 * Created by admin on  27/09/2017
 * Last modified 19:17 26/09/17
 */

package businessLogic.graph.filters;

import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

import businessLogic.common.interfaces.IHandler;
import businessLogic.common.interfaces.ILog;
import businessLogic.graph.executors.interfaces.ISourceExecutor;
import businessLogic.graph.filters.interfaces.IRenderFilter;
import businessLogic.graph.filters.interfaces.ISourceFilter;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.graph.filters.</P>
 * <P>A filter that implements {@link ISourceFilter}.
 * The filter contains a {@link ISourceExecutor} and at the start of 'Graph' pattern.</P>
 *
 * @param <TOutput> The type of data that received from the {@link ISourceExecutor}
 *                  and moved to the next {@link IRenderFilter}.
 * @see ISourceFilter
 * @see BaseFilter
 */


public class SourceFilter<TOutput> extends BaseFilter implements ISourceFilter<TOutput> {

    //region Fields

    private final ISourceExecutor<TOutput> m_sourceExecutor;

    private List<IHandler<TOutput>> m_followerFilters;

    private IHandler<TOutput> m_sourceHandler;

    //endregion

    //region Constructors

    public SourceFilter(ISourceExecutor<TOutput> sourceExecutor, @NonNull ILog logger) {
        this(sourceExecutor, logger, "");
    }

    public SourceFilter(ISourceExecutor<TOutput> sourceExecutor, @NonNull ILog logger, String id) {
        super(sourceExecutor, logger, id);
        m_sourceExecutor = sourceExecutor;
    }

    //endregion

    //region IBackgroundOperation implementation
    @Override
    protected void internalInitialize() throws Exception {
        m_followerFilters=null;
        m_sourceHandler = new SourceHandler();

        super.internalInitialize();
    }

    @Override
    protected void internalStart() throws Exception {
        if (m_followerFilters == null) {
            String msg = "ISourceFilter<TOutput> is null.";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }

        m_sourceExecutor.setSourceHandler(m_sourceHandler);

        super.internalStart();
    }

    //endregion

    //region IFilter implementation

    @Override
    public void setFollowerFilter(IHandler<TOutput>...followerFilter) {
        if(followerFilter==null || followerFilter.length==0){
            return;
        }
        m_followerFilters = Arrays.asList(followerFilter);
    }

    //endregion

    //region Nested classes
    private class SourceHandler implements IHandler<TOutput> {

        @Override
        public void setInput(TOutput input) {
            for (IHandler<TOutput> followerFilter : m_followerFilters) {
                followerFilter.setInput(input);
            }
        }
    }
    //endregion

}

