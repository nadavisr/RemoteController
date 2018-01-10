/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.graph.executors.interfaces;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.graph.executors.interfaces.</P>
 * <P>Defines an executor that has the ability to receive one specific type of data,
 * and distribute another type of data.</P>
 *
 * @param <TInput> The type of data that received to the executor.
 * @param <TOutput> The type of data that comes out from the executor.
 * @see ISourceExecutor
 * @see IRenderExecutor
 */

public interface ITransformExecutor<TInput, TOutput> extends ISourceExecutor<TOutput> , IRenderExecutor<TInput>{
}
