/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.common;

import android.support.annotation.NonNull;

import java.util.UUID;

import businessLogic.common.interfaces.IBackgroundOperation;
import businessLogic.common.interfaces.ILog;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.common.</P>
 * <P>Defines a base for {@link IBackgroundOperation}.</P>
 *
 * @see IBackgroundOperation
 */

public abstract class BaseBgOperation implements IBackgroundOperation {

    //region Fields

    protected final ILog m_logger;

    private final String m_id;

    protected BgOperationState m_bgOperationState;

    private boolean m_disposed;

    //endregion

    //region Constructors

    protected BaseBgOperation(@NonNull ILog logger, String id) {
        m_bgOperationState = BgOperationState.NotReady;
        m_logger = logger;
        if (id == null || id.isEmpty()) {
            m_id = generateDefaultID();
        } else {
            m_id = id;
        }
        m_disposed = false;
    }

    protected BaseBgOperation(@NonNull ILog logger) {
        this(logger, "");
    }

    private String generateDefaultID() {
        String className = getClass().getSimpleName();
        String uuid = UUID.randomUUID().toString();
        return className + "-" + uuid;
    }

    //endregion

    //region Getters

    @Override
    public String getId() {
        return m_id;
    }

    @Override
    public BgOperationState getState() {
        return m_bgOperationState;
    }

    @Override
    public ILog getLogger() {
        return m_logger;
    }

    //endregion

    //region IBackgroundOperation implementation

    @Override
    public void initialize() {
        if (m_bgOperationState == BgOperationState.Ready ||
                m_bgOperationState == BgOperationState.Running) {
            return;
        }

        if (m_bgOperationState == BgOperationState.Error) {
            m_logger.warning("BackgroundOperation initialize method called while the state is Error! ID: " + m_id);
            return;
        }

        m_logger.info(m_id + " BackgroundOperation initializing, State: " + m_bgOperationState);
        m_bgOperationState = BgOperationState.NotReady;

        try {
            internalInitialize();
            m_bgOperationState = BgOperationState.Ready;
            m_logger.info(m_id + " BackgroundOperation initialized. State: " + m_bgOperationState);
        } catch (Exception ex) {
            m_bgOperationState = BgOperationState.Error;
            String errorMessage = "Failed to initializeConfiguration the BackgroundOperation! ID: " + m_id +
                    ", State: " + m_bgOperationState;
            m_logger.error(errorMessage, ex);
        }
    }

    @Override
    public void start() {
        if (m_bgOperationState == BgOperationState.Running) {
            return;
        }

        if (m_bgOperationState == BgOperationState.Error) {
            m_logger.warning("BackgroundOperation start method called while the state is Error! ID: " + m_id);
            return;
        }

        m_logger.info(m_id + " BackgroundOperation starting, State: " + m_bgOperationState);

        if (m_bgOperationState != BgOperationState.Ready) {
            m_bgOperationState = BgOperationState.Error;
            String errorMessage = "Failed to start the BackgroundOperation because it was not in a ready state! ID: "
                    + m_id + ", State: " + m_bgOperationState;
            m_logger.error(errorMessage);
        }

        try {
            internalStart();
            m_bgOperationState = BgOperationState.Running;
            m_logger.info(m_id + " BackgroundOperation started. State: " + m_bgOperationState);
        } catch (Exception ex) {
            m_bgOperationState = BgOperationState.Error;
            String errorMessage = "Failed to start the BackgroundOperation! ID: " + m_id + ", State: "
                    + m_bgOperationState;
            m_logger.error(errorMessage, ex);
        }
    }

    @Override
    public void stop() {
        if (m_bgOperationState == BgOperationState.Ready) {
            return;
        }

        if (m_bgOperationState == BgOperationState.Error) {
            m_logger.warning("BackgroundOperation stop method called while the state is Error! ID: " + m_id);
            return;
        }

        m_logger.info(m_id + " BackgroundOperation stopping, State: " + m_bgOperationState);

        m_bgOperationState = BgOperationState.Ready;

        try {
            internalStop();
            m_bgOperationState = BgOperationState.Ready;
            m_logger.info(m_id + " BackgroundOperation stopped. State: " + m_bgOperationState);
        } catch (Exception ex) {
            m_bgOperationState = BgOperationState.Error;
            String errorMessage = "Failed to stop the BackgroundOperation! ID: " + m_id + ", State: " + m_bgOperationState;
            m_logger.error(errorMessage, ex);
        }
    }

    //endregion

    //region IDisposable Implementation

    @Override
    public void dispose() {
        dispose(true);
    }

    /**
     * <P>This method called by {@link IBackgroundOperation#dispose()}, use it!</P>
     * <P>Dispose method with option to dispose all, or only unmanaged resources.</P>
     *
     * @param disposing set true to dispose managed resources,
     *                  and in fact call innerDispose() method.
     */
    protected void dispose(boolean disposing) {
        if (m_disposed) {
            return;
        }

        if (disposing) {
            stop();
            if (m_bgOperationState == BgOperationState.Error) {
                String msg = "Error trying to stop a BackgroundOperation while disposing! ID: "
                        + m_id + ", State: " + m_bgOperationState;
                m_logger.warning(msg);
            }
            try {
                innerDispose();
                m_bgOperationState = BgOperationState.NotReady;
            } catch (Exception ex) {
                m_bgOperationState = BgOperationState.Error;
                m_logger.warning("Failed to dispose the managed resources of BackgroundOperation! ID: "
                        + m_id + ", State: " + m_bgOperationState, ex);
            }
        }
        unmanagedDispose();
        m_disposed = true;
        m_logger.info("BackgroundOperation: " + m_id + " disposed! ID: " + m_id + ", State: " + m_bgOperationState);
    }

    /**
     * <P>EMPTY protected method for dispose unmanaged resources,
     * which called from {@link IBackgroundOperation#dispose()}.</P>
     * <P>If this method overwritten,
     * you have to implement finalizer with the call {@link IBackgroundOperation#dispose()}
     * with false parameter.</P>
     */
    @SuppressWarnings("EmptyMethod")
    protected void unmanagedDispose() {

    }


//    /**
//     * Calls the dispose methods which will do its cleanup only
//     * if the dispose method was not already called by the user.
//     * @throws Throwable
//     */
//    @Override
//    protected void finalize() throws Throwable {
//        dispose(false);
//        super.finalize();
//    }


    //endregion

    //region Abstract Methods

    /**
     * Implements the actual initialization logic in inheriting background operation.
     *
     * @throws Exception Any error that may occur when the
     *                   {$link {@link IBackgroundOperation}} is initialized.
     */
    protected abstract void internalInitialize() throws Exception;

    /**
     * Implements the actual starting logic in inheriting background operation.
     *
     * @throws Exception Any error that may occur when the
     *                   {$link {@link IBackgroundOperation}} is started.
     */
    protected abstract void internalStart() throws Exception;

    /**
     * Implements the actual stopping logic in inheriting background operation.
     *
     * @throws Exception Any error that may occur when the
     *                   {$link {@link IBackgroundOperation}} is stopped.
     */
    protected abstract void internalStop() throws Exception;

    /**
     * Implements the actual managed disposing in inheriting background operation.
     *
     * @throws Exception Any error that may occur when the
     *                   {$link {@link IBackgroundOperation}} is dispose managed resources.
     */
    protected abstract void innerDispose() throws Exception;

    //endregion

}
