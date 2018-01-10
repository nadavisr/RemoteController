/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.graph.executors;

import android.support.annotation.NonNull;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import businessLogic.common.BaseBgOperation;
import businessLogic.common.BgOperationState;
import businessLogic.common.LongRunStoppableThread;
import businessLogic.common.exceptions.NotRunningBackgroundOperationException;
import businessLogic.common.interfaces.IHandler;
import businessLogic.common.interfaces.ILog;
import businessLogic.graph.executors.interfaces.ITransformExecutor;


/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.graph.executors.</P>
 * <P>Defines an executor that contains data and implements a producer consumer protocol.</P>
 *
 * @param <TData> The type of data that received to the executor, and comes out from the executor.
 * @see ITransformExecutor
 * @see BaseBgOperation
 */

public class ProducerConsumerExecutor<TData> extends BaseBgOperation implements ITransformExecutor<TData, TData> {

    //region Fields

    private final static short DEFAULT_QUEUE_SIZE = 10;

    private final static int TIME_OUT_MILLISECONDS = 1000;

    private final int m_blockingQueueCapacity;

    private BlockingQueue<TData> m_blockingQueue;

    private ConsumerThread m_consumerThread;

    private IHandler<TData> m_sourceHandler;

    private IHandler<Exception> m_exceptionHandler;
    //endregion

    //region Constructors

    public ProducerConsumerExecutor(int blockingQueueCapacity, @NonNull ILog logger, String id) {
        super(logger, id);
        m_blockingQueueCapacity = blockingQueueCapacity;
    }

    public ProducerConsumerExecutor(int blockingQueueCapacity, @NonNull ILog logger) {
        this(blockingQueueCapacity, logger, "");
    }

    public ProducerConsumerExecutor(@NonNull ILog logger, String id) {
        this(DEFAULT_QUEUE_SIZE, logger, id);
    }

    public ProducerConsumerExecutor(@NonNull ILog logger) {
        this(DEFAULT_QUEUE_SIZE, logger, "");
    }

    //endregion

    //region BaseBgOperation implementation
    @Override
    protected void internalInitialize() {
        m_blockingQueue = new ArrayBlockingQueue<>(m_blockingQueueCapacity);
        m_logger.info("ProducerConsumerExecutor has been initialized with a queue with " + m_blockingQueueCapacity + " places");
    }

    @Override
    protected void internalStart() {
        if (m_sourceHandler == null) {
            String msg = "IHandler<TData> is null.";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }

        if (m_exceptionHandler == null) {
            String msg = "m_exceptionHandler is null.";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }
        m_consumerThread = new ConsumerThread();
        m_consumerThread.start();
    }

    @Override
    protected void internalStop() {
        m_consumerThread.stopRun();
        try {
            m_consumerThread.join(TIME_OUT_MILLISECONDS + TIME_OUT_MILLISECONDS / 2);
        } catch (InterruptedException ex) {
            String msg = "Failed to attempt to safely stop the consuming thread, interrupting the thread.";
            m_logger.warning(msg, ex);
            m_consumerThread.interrupt();
        }
        if (m_blockingQueue != null) {
            m_blockingQueue.clear();
        }
    }

    @Override
    protected void innerDispose() throws Exception {
        if (m_blockingQueue != null) {
            m_blockingQueue.clear();
            m_blockingQueue = null;
        }
        m_consumerThread = null;
    }

    //endregion

    //region ITransformExecutor<TData, TData> implementation
    @Override
    public void setInput(TData data) throws NotRunningBackgroundOperationException {
        if (m_bgOperationState != BgOperationState.Running) {
            NotRunningBackgroundOperationException ex = new NotRunningBackgroundOperationException(this);
            m_logger.error(ex.getMessage());
            throw ex;
        }

        if (!m_blockingQueue.isEmpty() && m_blockingQueue.remainingCapacity() == 0) {
            m_blockingQueue.remove();
            m_logger.warning("Data was received and the queue is full, so the first place was thrown out of the queue, in IExecutor: "
                    + ProducerConsumerExecutor.this.getId());
        }
        m_blockingQueue.add(data);
        if (m_blockingQueue.size() == m_blockingQueueCapacity) {
            String msg = "The queue is full! if more data arrives before a place is cleared," +
                    " it will cause information to be lost, in IExecutor: " + ProducerConsumerExecutor.this.getId();
            m_logger.warning(msg);
        }
    }

    @Override
    public void setExceptionHandler(IHandler<Exception> exceptionHandler) {
        m_exceptionHandler = exceptionHandler;
    }

    @Override
    public void setSourceHandler(IHandler<TData> sourceHandler) {
        m_sourceHandler = sourceHandler;
    }
    //endregion

    //region Nested classes

    private class ConsumerThread extends LongRunStoppableThread {

        int m_count = 0;

        @Override
        protected boolean internalRun() {
            if (m_blockingQueue == null) {
                return false;
            }
            try {
                TData data = m_blockingQueue.poll(TIME_OUT_MILLISECONDS, TimeUnit.MILLISECONDS);
                if (data != null) {
                    m_sourceHandler.setInput(data);
                    m_count = 0;
                    return true;
                }

                m_count++;
                if (m_count == 100) {
                    final String msg = "The queue was empty for " + m_count * TIME_OUT_MILLISECONDS +
                            " milliseconds, in IExecutor: " + ProducerConsumerExecutor.this.getId();
                    m_logger.verbose(msg);
                    m_count = 0;
                }
                return true;

            } catch (InterruptedException ex) {
                m_logger.warning("Consuming interrupted in IExecutor: " + ProducerConsumerExecutor.this.getId(), ex);
                return false;
            }
        }
    }

    //endregion
}

