/*
 * Created by admin on 22/10/2017
 * Last modified 13:27 22/10/17
 */

package services.common;

import android.support.annotation.IntRange;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import businessLogic.common.BgOperationState;
import businessLogic.common.exceptions.NotRunningBackgroundOperationException;
import businessLogic.common.interfaces.IBackgroundOperation;
import businessLogic.common.interfaces.IExceptionDistributor;
import businessLogic.common.interfaces.IHandler;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.common.</P>
 * <P>A {@link IBackgroundOperation} that executes {@link Runnable} classes,
 * which executes one after the other according to the received order.
 * Implements also {@link IHandler} and {@link IExceptionDistributor} </P>
 *
 * @see ServicesBaseBgOperation
 */

public class RunnableSynchronizedExecutor extends ServicesBaseBgOperation implements IHandler<Runnable>, IExceptionDistributor<Exception> {

    //region Fields

    private ExecutorService m_executorService;

    private IHandler<Exception> m_exceptionHandler;

    //endregion

    //region Constructors

    public RunnableSynchronizedExecutor(String id) {
        super(id);
    }

    public RunnableSynchronizedExecutor() {
        this("");
    }


    //endregion

    //region Methods

    //region ServicesBaseBgOperation implementation

    @Override
    protected void internalInitialize() {
        m_exceptionHandler = null;
    }

    @Override
    protected void internalStart() {
        if (m_exceptionHandler == null) {
            String msg = "m_exceptionHandler is null.";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }
        m_executorService = Executors.newSingleThreadExecutor();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void internalStop() {
        List<Runnable> runnables = m_executorService.shutdownNow();
        if (!m_executorService.isShutdown()) {
            m_logger.warning("Executor service do not shutted down successfully.");
            return;
        }

        m_executorService = null;

        String msg = "Executor Service shutted down successfully," +
                "the amount of runnables that has not started:" + runnables.size();


        m_logger.info(msg);

    }

    @Override
    protected void innerDispose() throws Exception {
        m_exceptionHandler = null;
    }

    //endregion

    //region IHandler<Runnable> implementation

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public void setInput(Runnable data) throws NotRunningBackgroundOperationException {

        if (m_bgOperationState != BgOperationState.Running) {
            NotRunningBackgroundOperationException ex = new NotRunningBackgroundOperationException(this);
            m_logger.error(ex.getMessage());
            throw ex;
        }

        if (data != null) {
            m_executorService.execute(data);
        }
    }

    //endregion 

    //region IExceptionDistributor<Exception> implementation

    @Override
    public void setExceptionHandler(IHandler<Exception> exceptionHandler) {
        m_exceptionHandler = exceptionHandler;
    }

    //endregion

    /**
     * The method stops the {@link IBackgroundOperation},
     * but gives time to finish executing the {@link Runnable}.
     *
     * @param timeToFinishInMilliseconds The maximum number of milliseconds for the
     *                                   {@link Runnable}s to be completed before it stopped.
     */
    public void synchronizedStop(@IntRange(from = 1) long timeToFinishInMilliseconds) {

        if (m_bgOperationState == BgOperationState.Ready) {
            return;
        }
        m_logger.info(getId() + " BackgroundOperation stopping, State: " + m_bgOperationState);

        m_bgOperationState = BgOperationState.Ready;

        try {
            internalSynchronizedStop(timeToFinishInMilliseconds);
            internalStop();
            m_bgOperationState = BgOperationState.Ready;
            m_logger.info(getId() + " BackgroundOperation stopped. State: " + m_bgOperationState);
        } catch (Exception ex) {
            m_bgOperationState = BgOperationState.Error;
            String errorMessage = "Failed to stop the BackgroundOperation! ID: " + getId() + ", State: " + m_bgOperationState;
            m_logger.error(errorMessage, ex);
        }
    }

    private void internalSynchronizedStop(long timeOutInMillis) throws Exception {
        try {
            m_executorService.awaitTermination(timeOutInMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            m_logger.error("ExecutorService termination interrupted! ID: " + getId() + ", State: " + m_bgOperationState, e);
            throw e;
        }
    }
    //endregion
}
