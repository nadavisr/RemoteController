/*
 * Created by admin on  27/09/2017
 * Last modified 19:17 26/09/17
 */

package businessLogic.graph.filters.interfaces;

import businessLogic.graph.executors.interfaces.ITransformExecutor;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.graph.filters.interfaces.</P>
 * <P>Defines a filter that contains a {@link ITransformExecutor} and at the middle of 'Graph' pattern.</P>
 *
 * @param <TOutput> The type of the data that received from the previous {@link ISourceFilter}.
 * @param <TInput>  The type of the data that comes out to the next {@link IRenderFilter}.
 * @see ISourceFilter
 * @see IRenderFilter
 */

public interface ITransformerFilter<TInput, TOutput> extends IRenderFilter<TInput>, ISourceFilter<TOutput> {

}
