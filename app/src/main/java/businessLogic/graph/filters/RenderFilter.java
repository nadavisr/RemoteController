/*
 * Created by admin on  27/09/2017
 * Last modified 19:17 26/09/17
 */

package businessLogic.graph.filters;

import android.support.annotation.NonNull;

import businessLogic.common.BgOperationState;
import businessLogic.common.interfaces.ILog;
import businessLogic.graph.executors.interfaces.IRenderExecutor;
import businessLogic.graph.filters.interfaces.IRenderFilter;
import businessLogic.graph.filters.interfaces.ISourceFilter;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.graph.filters</P>
 * <P>A filter that implements {@link IRenderFilter}.
 * The filter contains a {@link IRenderExecutor} and at the end of 'Graph' pattern.</P>
 *
 * @param <TInput> The type of data that received from the previous {@link ISourceFilter}.
 * @see IRenderFilter
 * @see BaseFilter
 */

public class RenderFilter<TInput> extends BaseFilter implements IRenderFilter<TInput> {

    //region Fields

    private final IRenderExecutor<TInput> m_renderExecutor;

    //endregion

    //region Constructors

    public RenderFilter(IRenderExecutor<TInput> renderExecutor, @NonNull ILog logger) {
        this(renderExecutor, logger, "");
    }

    public RenderFilter(IRenderExecutor<TInput> renderExecutor, @NonNull ILog logger, String id) {
        super(renderExecutor, logger, id);
        m_renderExecutor = renderExecutor;
    }

    //endregion

    //region IFilter implementation

    @Override
    public void setInput(TInput input) {
        if (m_bgOperationState != BgOperationState.Running) {
            String msg = "The Filter: " + getId() + " received input while the status: " + m_bgOperationState;
            m_logger.warning(msg);
            return;
        }
        m_renderExecutor.setInput(input);
    }

    //endregion


}
