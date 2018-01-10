/*
 * Created by Romaa on 18/12/2017
 * Last modified 11:10 18/12/17
 */

package businessLogic.common.interfaces;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.common.interfaces.</P>
 * <P>The interface defines a UI that render the view, and can start and stop the rendering.</P>
 */
public interface IRendering {

    boolean startRendering();

    void stopRendering();
}
