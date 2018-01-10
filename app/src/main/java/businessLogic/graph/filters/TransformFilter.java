/*
 * Created by admin on  27/09/2017
 * Last modified 19:17 26/09/17
 */

package businessLogic.graph.filters;

import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

import businessLogic.common.BgOperationState;
import businessLogic.common.interfaces.IHandler;
import businessLogic.common.interfaces.ILog;
import businessLogic.graph.executors.interfaces.ITransformExecutor;
import businessLogic.graph.filters.interfaces.IRenderFilter;
import businessLogic.graph.filters.interfaces.ISourceFilter;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.graph.filters.interfaces.</P>
 * <P>A filter that implements {@link ISourceFilter} and {@link IRenderFilter}.
 * The filter that contains a {@link ITransformExecutor} and at the middle of 'Graph' pattern.</P>
 *
 * @param <TOutput> The type of the data that received from the previous {@link ISourceFilter}.
 * @param <TInput>  The type of the data that comes out to the next {@link IRenderFilter}.
 * @see ISourceFilter
 * @see IRenderFilter
 */

public class TransformFilter<TInput, TOutput> extends BaseFilter implements IRenderFilter<TInput>, ISourceFilter<TOutput> {

    //region Fields

    private final ITransformExecutor<TInput, TOutput> m_transformExecutor;

    private List<IHandler<TOutput>> m_followerFilters;

    private IHandler<TOutput> m_sourceHandler;

    //endregion

    //region Constructors

    public TransformFilter(ITransformExecutor<TInput, TOutput> transformExecutor, @NonNull ILog logger) {
        this(transformExecutor, logger, "");

    }

    public TransformFilter(ITransformExecutor<TInput, TOutput> sourceExecutor, @NonNull ILog logger, String id) {
        super(sourceExecutor, logger, id);
        m_transformExecutor = sourceExecutor;
    }

    //endregion

    //region IBackgroundOperation implementation

    @Override
    protected void internalInitialize() throws Exception {
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

        m_transformExecutor.setSourceHandler(m_sourceHandler);

        super.internalStart();
    }

    //endregion

    //region IFilter implementation

    @Override
    public void setFollowerFilter(IHandler<TOutput>... followerFilter) {

        if (followerFilter == null || followerFilter.length == 0) {
            return;
        }
        m_followerFilters = Arrays.asList(followerFilter);
    }

    @Override
    public void setInput(TInput tOutput) {
        if (m_bgOperationState != BgOperationState.Running) {
            String msg = "The Filter: " + getId() + " received input while the status: " + m_bgOperationState;
            m_logger.warning(msg);
            return;
        }

        m_transformExecutor.setInput(tOutput);
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
