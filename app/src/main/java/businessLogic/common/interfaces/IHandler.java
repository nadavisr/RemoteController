/*
 * Created by admin on 27/09/2017
 * Last modified 17:53 27/09/17
 */

package businessLogic.common.interfaces;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.common.interfaces.</P>
 * <P>The interface defines a class the handling typed input.</P>
 *
 * @param <TData> The type of handling data.
 */
public interface IHandler<TData> {

    /**
     * Set data to the handler.
     */
    void setInput(TData data);
}
