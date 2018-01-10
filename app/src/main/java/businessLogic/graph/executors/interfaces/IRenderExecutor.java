/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.graph.executors.interfaces;

import businessLogic.common.interfaces.IHandler;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.graph.executors.interfaces.</P>
 * <P>Defines an executor that has the ability to handle a certain type of data.</P>
 *
 * @param <TInput> The type of data that received to the executor.
 * @see IExecutor
 * @see IHandler
 */


public interface IRenderExecutor<TInput> extends IExecutor,IHandler<TInput> {

}
