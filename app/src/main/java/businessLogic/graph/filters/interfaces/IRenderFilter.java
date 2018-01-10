/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.graph.filters.interfaces;

import businessLogic.common.interfaces.IHandler;
import businessLogic.graph.executors.interfaces.IRenderExecutor;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.graph.filters.interfaces.</P>
 * <P>Defines a filter that contains a {@link IRenderExecutor} and at the end of 'Graph' pattern.</P>
 *
 * @param <TInput> The type of data that received from the previous {@link ISourceFilter}.
 * @see IFilter
 * @see IHandler
 */

public interface IRenderFilter<TInput> extends IFilter, IHandler<TInput> {
}
