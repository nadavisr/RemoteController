/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.communication.interfaces;


import businessLogic.common.interfaces.IBackgroundOperation;
import businessLogic.common.interfaces.IHandler;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.communication.interfaces.</P>
 * <P>Defines the ability to subscribe messages of a certain type.</P>
 *
 * @param <TData> The type of subscribed data.
 * @see IBackgroundOperation
 */

public interface ISubscriber<TData> extends IBackgroundOperation {

    void subscribe(IHandler<TData> handler);

    void unsubscribe();

    void setExceptionHandler(IHandler<Exception> exceptionHandler);
}
