/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.communication.interfaces;

import java.io.IOException;

import businessLogic.common.interfaces.IBackgroundOperation;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.communication.interfaces.</P>
 * <P>Defines the ability to publish messages of a certain type.</P>
 *
 * @param <TData> The type of published data.
 * @see IBackgroundOperation
 */

public interface IPublisher<TData> extends IBackgroundOperation {
    void publish(TData data) throws IOException;
}
