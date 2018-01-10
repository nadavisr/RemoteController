/*
 * Created by admin on 03/12/2017
 * Last modified 17:55 12/11/17
 */

package businessLogic.graph.executors;

import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import businessLogic.common.BaseBgOperation;
import businessLogic.common.BgOperationState;
import businessLogic.common.exceptions.NotRunningBackgroundOperationException;
import businessLogic.common.interfaces.IHandler;
import businessLogic.common.interfaces.ILog;
import businessLogic.graph.executors.interfaces.ITransformExecutor;


/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.graph.executors.</P>
 * <P>Defines an executor that contains two {@link ProducerConsumerExecutor}</P>
 *
 * @param <TData> The type of data that received to the executor, and comes out from the executor.
 * @see ITransformExecutor
 * @see BaseBgOperation
 */

public class ProducerDoubleConsumerExecutor<TData> extends BaseBgOperation implements ITransformExecutor<TData, TData> {

    private ProducerConsumerExecutor<TData> m_firstProducerConsumerExecutor;

    private ProducerConsumerExecutor<TData> m_secondProducerConsumerExecutor;

    private IHandler<TData> m_firstSourceHandler;

    private IHandler<TData> m_secondSourceHandler;

    private ExecutorService m_firstExecutorService;

    private ExecutorService m_secondExecutorService;

    private boolean m_setFirst;

    protected ProducerDoubleConsumerExecutor(@NonNull ILog logger, String id) {
        super(logger, id);
        m_setFirst = true;
    }

    protected ProducerDoubleConsumerExecutor(@NonNull ILog logger) {
        super(logger);
        m_setFirst = true;
    }

    @Override
    public void setInput(TData tData) {
        if (m_bgOperationState != BgOperationState.Running) {
            NotRunningBackgroundOperationException ex = new NotRunningBackgroundOperationException(this);
            m_logger.error(ex.getMessage());
            throw ex;
        }

        if (m_firstSourceHandler != null) {
            m_firstExecutorService.execute(() -> m_firstProducerConsumerExecutor.setInput(tData));
        }
        if (m_secondSourceHandler != null) {
            m_secondExecutorService.execute(() -> m_secondProducerConsumerExecutor.setInput(tData));
        }
    }

    @Override
    public void setExceptionHandler(IHandler<Exception> exceptionHandler) {
        m_firstProducerConsumerExecutor.setExceptionHandler(exceptionHandler);
        m_secondProducerConsumerExecutor.setExceptionHandler(exceptionHandler);
    }

    @Override
    public void setSourceHandler(IHandler<TData> sourceHandler) {
        if (sourceHandler == null) {
            return;
        }

        if (m_firstSourceHandler == null) {
            setFirstSourceHandler(sourceHandler);
        } else if (m_secondSourceHandler == null) {
            setSecondSourceHandler(sourceHandler);
        } else if (m_setFirst) {
            setFirstSourceHandler(sourceHandler);
        } else {
            setSecondSourceHandler(sourceHandler);
        }
    }


    public void setFirstSourceHandler(IHandler<TData> sourceHandler) {
        m_firstSourceHandler = sourceHandler;
        m_firstProducerConsumerExecutor.setSourceHandler(sourceHandler);
        m_setFirst = false;
    }

    public void setSecondSourceHandler(IHandler<TData> sourceHandler) {
        m_secondSourceHandler = sourceHandler;
        m_secondProducerConsumerExecutor.setSourceHandler(sourceHandler);
        m_setFirst = true;
    }

    @Override
    protected void internalInitialize() throws Exception {
        m_firstProducerConsumerExecutor = new ProducerConsumerExecutor<>(m_logger, "first-" + getId());
        m_secondProducerConsumerExecutor = new ProducerConsumerExecutor<>(m_logger, "second-" + getId());

        m_firstProducerConsumerExecutor.initialize();
        m_secondProducerConsumerExecutor.initialize();

        if (m_firstProducerConsumerExecutor.getState() != BgOperationState.Ready
                || m_secondProducerConsumerExecutor.getState() != BgOperationState.Ready) {
            throw new Exception("At least on of the internal producer consumer executors failed on try to initialize.");
        }

        m_firstExecutorService = Executors.newSingleThreadExecutor();
        m_secondExecutorService = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void internalStart() throws Exception {
        m_firstProducerConsumerExecutor.start();
        m_secondProducerConsumerExecutor.start();

        if (m_firstProducerConsumerExecutor.getState() != BgOperationState.Running
                || m_secondProducerConsumerExecutor.getState() != BgOperationState.Running) {
            throw new Exception("At least on of the internal producer consumer executors failed on try to start.");
        }
    }

    @Override
    protected void internalStop() throws Exception {
        m_firstProducerConsumerExecutor.stop();
        m_secondProducerConsumerExecutor.stop();

        if (m_firstProducerConsumerExecutor.getState() != BgOperationState.Ready
                || m_secondProducerConsumerExecutor.getState() != BgOperationState.Ready) {
            throw new Exception("At least on of the internal producer consumer executors failed on try to stop.");
        }
    }

    @Override
    protected void innerDispose() throws Exception {
        m_firstProducerConsumerExecutor.stop();
        m_secondProducerConsumerExecutor.stop();

        if (m_firstProducerConsumerExecutor.getState() != BgOperationState.Ready
                || m_secondProducerConsumerExecutor.getState() != BgOperationState.Ready) {
            m_logger.warning("At least on of the internal producer consumer executors failed on try to dispose.");
        }

        m_firstProducerConsumerExecutor = m_secondProducerConsumerExecutor = null;
        m_firstSourceHandler = m_secondSourceHandler = null;
    }


    //endregion
}

