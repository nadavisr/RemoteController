/*
 * Created by admin on 27/09/2017
 * Last modified 17:53 27/09/17
 */

package businessLogic.common.interfaces;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.common.interfaces.</P>
 * <P>The interface defines a class that distributes an exceptions to handler</P>
 *
 * @param <TException> A class that extends {@link Exception}.
 * @see IHandler
 * @see Exception
 */

public interface IExceptionDistributor<TException extends Exception> {

    /**
     * Setter of {@link IHandler}, which will receive the distributed {@link Exception}.
     *
     * @param exceptionHandler The {@link IHandler} of the distributed data.
     */
    void setExceptionHandler(IHandler<TException> exceptionHandler);
}


