/*
 * Created by admin on 27/09/2017
 * Last modified 17:53 27/09/17
 */

package businessLogic.common.interfaces;

import businessLogic.common.BgOperationState;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.common.interfaces.</P>
 * <P>The interface defines a background operation which is defined by the actions:
 * {@link IBackgroundOperation#initialize()} , {@link IBackgroundOperation#start()} , {@link IBackgroundOperation#stop()}.
 * Inherit from {@link IDisposable}. </P>
 * @see IDisposable
 */

public interface IBackgroundOperation extends IDisposable {

    /**
     * @return The ID of {@link IBackgroundOperation}.
     */
    String getId();

    /**
     * @return The State of {@link IBackgroundOperation}.
     * @see BgOperationState enum.
     */
    BgOperationState getState();

    /**
     * @return The logger of {@link IBackgroundOperation}.
     * @see ILog interface.
     */
    ILog getLogger();

    /**
     * The method initializes the {@link IBackgroundOperation},
     * A precondition to use {@link IBackgroundOperation#start()} method.
     */
    void initialize();

    /**
     * The method starts the {@link IBackgroundOperation}.
     * A precondition to use this method, is to call {@link IBackgroundOperation#initialize()} method.
     */
    void start();

    /**
     * The method stops the {@link IBackgroundOperation}.
     */
    void stop();

}
