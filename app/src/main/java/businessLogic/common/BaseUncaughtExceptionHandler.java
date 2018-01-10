/*
 * Created by admin on 28/09/2017
 * Last modified 13:55 28/09/17
 */

package businessLogic.common;

import android.support.annotation.NonNull;

import businessLogic.common.interfaces.ILog;
import businessLogic.common.interfaces.IUncaughtExceptionHandler;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.common.</P>
 * <P>Define a base class fpr {@link IUncaughtExceptionHandler}.</P>
 */

public abstract class BaseUncaughtExceptionHandler implements IUncaughtExceptionHandler {

    //region Fields

    private final Thread.UncaughtExceptionHandler m_uncaughtExceptionHandler;

    final protected ILog m_logger;

    //endregion

    //region Constructors

    protected BaseUncaughtExceptionHandler(@NonNull ILog logger) {
        m_logger = logger;

        m_uncaughtExceptionHandler = this::handleUncaughtException;
    }

    //endregion

    //region IUncaughtExceptionHandler Implementation

    /**
     * Do caught unhandled exception initialization
     */
    @Override
    public void initializeUncaughtExceptionHandler() throws Exception {

        Thread.setDefaultUncaughtExceptionHandler(m_uncaughtExceptionHandler);
        m_logger.info("Initialized default handler for uncaught exceptions.");
    }

    //endregion

    //region Methods

    private void handleUncaughtException(Thread thread, Throwable throwable) {
        String threadDetails = "Thread: Name=" + thread.getName() + ", ID=" + thread.getId();
        try {
            m_logger.fatal("Caught unhandled exception in " + threadDetails, throwable);
            innerHandleUncaughtException(thread,throwable);
        } finally {
            m_logger.fatal("Shutting down the application...");
            runClosingSequence(thread, throwable);
        }
    }

    /**
     * Runs the closing sequence according to the type of the thread.
     * @param thread The thread caught in it unhandled exception.
     * @param throwable The uncaught exception.
     */
    protected abstract void runClosingSequence(Thread thread, Throwable throwable);

    /**
     * Specific actions to do when handled uncaught exception.
     * @param thread The thread caught in it unhandled exception.
     * @param throwable The uncaught exception.
     */
    protected abstract void innerHandleUncaughtException(Thread thread, Throwable throwable);

    //endregion
}


