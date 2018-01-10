/*
 * Created by admin on 28/09/2017
 * Last modified 13:53 28/09/17
 */

package businessLogic.common.interfaces;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.common.interfaces.</P>
 * <P>Defines handler of uncaught exceptions.</P>
 */

public interface IUncaughtExceptionHandler  {

    /**
     * Register the caller to caught unhandled exception.
     *
     * @exception Exception Throw exception if initialization failed.
     */
    void initializeUncaughtExceptionHandler() throws Exception;
}
