/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.graph.filters.interfaces;

import businessLogic.common.interfaces.IHandler;
import businessLogic.graph.executors.interfaces.ISourceExecutor;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.graph.filters.interfaces.</P>
 * <P>Defines a filter that contains a {@link ISourceExecutor} and at the start of 'Graph' pattern.</P>
 *
 * @param <TOutput> The type of data that received from the {@link ISourceExecutor}
 *                  and moved to the next {@link IRenderFilter}.
 * @see IFilter
 */

public interface ISourceFilter<TOutput> extends IFilter {

    /**
     * Setter of the next {@link IFilter} , which will receive the distributed data.
     *
     * @param followerFilter The next {@link IFilter}s in the 'Graph' pattern, the {@link IFilter}
     *                       must implements {@link IHandler}.
     */
    void setFollowerFilter(IHandler<TOutput>...followerFilter);
}
