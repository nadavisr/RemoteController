/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.graph.executors.interfaces;

import businessLogic.common.interfaces.IHandler;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.graph.executors.interfaces.</P>
 * <P>Defines an executor that has the ability to distribute certain type of data,
 * to some handler.</P>
 *
 * @param <TOutput> The type of data that comes out from the executor.
 * @see IExecutor
 */

public interface ISourceExecutor<TOutput> extends IExecutor {

    /**
     * Setter of {@link IHandler}, which will receive the distributed data.
     *
     * @param sourceHandler The {@link IHandler} of the distributed data.
     */
    void setSourceHandler(IHandler<TOutput> sourceHandler);

}
