/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.graph.filters;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import businessLogic.common.BaseBgOperation;
import businessLogic.common.BgOperationState;
import businessLogic.common.interfaces.IHandler;
import businessLogic.common.interfaces.ILog;
import businessLogic.graph.executors.interfaces.IExecutor;
import businessLogic.graph.filters.interfaces.IFilter;

import static businessLogic.common.BgOperationState.Error;
import static businessLogic.common.BgOperationState.Ready;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.graph.filters.</P>
 * <P>Defines a base for {@link IFilter},
 * that helps with exception handling of contained executors.</P>
 *
 * @see BaseBgOperation
 */

abstract class BaseFilter extends BaseBgOperation implements IFilter {

    //region Fields

    private final IExecutor m_executor;
    private IHandler<FilterException> m_exceptionHandler;
    private ExceptionHandler m_internalExceptionHandler;
    private ExecutorService m_executorService;

    //endregion

    //region Constructors

    protected BaseFilter(IExecutor executor, @NonNull ILog logger) {
        this(executor, logger, "");
    }

    protected BaseFilter(IExecutor executor, @NonNull ILog logger, String id) {
        super(logger, id);
        m_executor = executor;
    }

    //endregion

    //region IBackgroundOperation implementation

    @CallSuper
    @Override
    protected void internalInitialize() throws Exception {
        if (m_executor == null) {
            String msg = "IExecutor is null.";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }

        m_internalExceptionHandler = new ExceptionHandler();

        m_executor.initialize();

        if (m_executor.getState() == Error) {
            throw new Exception("The IExecutor: " + m_executor.getState() + ", is in Error state after a initialize attempt.");
        }
        m_executorService = Executors.newSingleThreadExecutor();


    }

    @CallSuper
    @Override
    protected void internalStart() throws Exception {
        if (m_exceptionHandler == null) {
            String msg = "IHandler<Exception> is null.";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }

        m_executor.setExceptionHandler(m_internalExceptionHandler);

        m_executor.start();
        if (m_executor.getState() == Error) {
            throw new Exception("The IExecutor: " + m_executor.getState() + ", is in Error state after a start attempt.");
        }

    }

    @Override
    protected void internalStop() throws Exception {
        m_executor.stop();
        if (m_executor.getState() == Error) {
            throw new Exception("The IExecutor: " + m_executor.getState() + ", is in Error state after a stop attempt.");
        }
    }

    @CallSuper
    @Override
    protected void innerDispose() throws Exception {
        if (m_executor == null) {
            return;
        }

        m_executor.dispose();
        if (m_executor.getState() == Error) {
            throw new Exception("The IExecutor: " + m_executor.getState() + ", is in Error state after a dispose attempt.");
        }
    }

    @Override
    public void setExceptionHandler(IHandler<FilterException> handler) {
        m_exceptionHandler = handler;
    }

    //endregion

    //region Methods

    private void restart() {
        m_logger.info("Trying to restart the IExecutor: " + m_executor.getId());

        m_executor.stop();
        if (m_executor.getState() != BgOperationState.Ready) {
            String msg = "Failed on trying to stop the IExecutor: " + m_executor.getId();
            m_logger.error(msg);
            stopOnException();
        }

        m_executor.start();
        if (m_executor.getState() != BgOperationState.Running) {
            String msg = "Failed on trying to start the IExecutor: " + m_executor.getId();
            m_logger.error(msg);
            stopOnException();
        }

        m_logger.info("the IExecutor: " + m_executor.getId() + "restarted successfully.");
    }

    protected void stopOnException() {

        stop();
        if (m_bgOperationState != Ready) {
            String msg = "Failed on trying to stop the filter: " + getId();
            m_logger.error(msg);
        }
        m_bgOperationState = BgOperationState.Error;

        String msg = "The filter: " + getId() + " status updated to Error.";
        m_logger.error(msg);
    }

    protected void setExceptionToHandler(final FilterException filterException) {
        m_executorService.execute(() -> m_exceptionHandler.setInput(filterException));
    }

//endregion

    //region Nested classes
    private class ExceptionHandler implements IHandler<Exception> {

        @Override
        public void setInput(Exception exception) {
            try {
                String msg = "An error has been received from the IExecutor: " + m_executor.getId()
                        + ", the IExecutor state:" + m_executor.getState() + ".";
                switch (m_executor.getState()) {
                    case Running:
                        m_logger.warning(msg, exception);
                        break;
                    case Ready:
                        m_logger.warning(msg, exception);
                        restart();
                        break;
                    case Error:
                        m_logger.error(msg, exception);
                        stopOnException();
                        break;
                }
            } catch (Exception internalException) {
                String msg = "An error has been received in internal exception handling.";
                m_logger.warning(msg, internalException);
            } finally {
                final FilterException filterException = new FilterException(BaseFilter.this, exception);
                m_executorService.execute(() -> m_exceptionHandler.setInput(filterException));
            }
        }

    }
//endregion
}
