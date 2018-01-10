/*
 * Created by admin on 03/10/2017
 * Last modified 16:06 03/10/17
 */

package businessLogic.common.interfaces;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.common.interfaces.</P>
 * <P>The interface defines a classes that must to be initialized before use.</P>
 */

public interface IInitializable {

    /**
     * The method initializes the class, a precondition to use other method.
     */
    void initialize();

    boolean isInitialized();

}
