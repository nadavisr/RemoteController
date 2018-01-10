/*
 * Created by admin on 22/10/2017
 * Last modified 13:21 22/10/17
 */

package services.common;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import businessLogic.common.BgOperationState;
import businessLogic.common.LongRunStoppableThread;
import businessLogic.common.exceptions.NotRunningBackgroundOperationException;
import businessLogic.common.interfaces.IBackgroundOperation;
import businessLogic.common.interfaces.IExceptionDistributor;
import businessLogic.common.interfaces.IHandler;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.common.</P>
 * <P>A {@link IBackgroundOperation} that executes {@link Runnable} classes,
 * which executes them one after the other according to the received order.
 * Implements also {@link IHandler} and {@link IExceptionDistributor}.</P>
 *
 * @see ServicesBaseBgOperation
 */

public class RunnableSynchronizedExecutor2 extends ServicesBaseBgOperation implements IHandler<Runnable>, IExceptionDistributor<Exception> {

    //region Fields

    private final static short DEFAULT_QUEUE_SIZE = 10;

    private final static int TIME_OUT_MILLISECONDS = 1000;

    private final int m_blockingQueueCapacity;

    private BlockingQueue<Future<?>> m_blockingQueue;

    private ExecutorService m_executorService;

    private ConsumerThread m_consumerThread;

    private IHandler<Exception> m_exceptionHandler;


    //endregion

    //region Constructors

    public RunnableSynchronizedExecutor2(int blockingQueueCapacity, String id) {
        super(id);
        m_blockingQueueCapacity = blockingQueueCapacity;
    }

    public RunnableSynchronizedExecutor2(int blockingQueueCapacity) {
        this(blockingQueueCapacity, "");
    }

    public RunnableSynchronizedExecutor2() {
        this(DEFAULT_QUEUE_SIZE, "");
    }

    //endregion

    //region Methods

    //region BaseBgOperation implementation
    @Override
    protected void internalInitialize() {
        m_blockingQueue = new ArrayBlockingQueue<>(m_blockingQueueCapacity);
        m_logger.info("ProducerConsumerExecutor has been initialized with a queue with " + m_blockingQueueCapacity + " places");

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

        m_consumerThread = new ConsumerThread();
        m_consumerThread.start();
    }

    @SuppressWarnings("ConstantConditions")
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

        List<Runnable> runnables = m_executorService.shutdownNow();

        if (!m_executorService.isShutdown()) {
            m_logger.warning("Executor service do not shutted down successfully.");
            return;
        }

        m_executorService = null;

        int numOfUnfinishedRunnables = runnables.size();


        if (m_blockingQueue != null) {
            numOfUnfinishedRunnables += m_blockingQueue.size();
            m_blockingQueue.clear();
        }
        String msg = "Executor Service shutted down successfully," +
                "the amount of runnables that has not started:" + numOfUnfinishedRunnables;

        m_logger.info(msg);
    }

    @Override
    protected void innerDispose() throws Exception {
        m_blockingQueue = null;

        m_consumerThread = null;
    }

    //endregion

    //region IHandler<Runnable> implementation

    @Override
    public synchronized void setInput(Runnable data) throws NotRunningBackgroundOperationException {

        if (m_bgOperationState != BgOperationState.Running) {
            NotRunningBackgroundOperationException ex = new NotRunningBackgroundOperationException(this);
            m_logger.error(ex.getMessage());
            throw ex;
        }

        if (data == null) {
            return;
        }

        if (!m_blockingQueue.isEmpty() && m_blockingQueue.remainingCapacity() == 0) {
            m_blockingQueue.remove();
            m_logger.warning("Data was received and the queue is full, so the first place was thrown out of the queue");
        }

        Future<?> future = m_executorService.submit(data);
        m_blockingQueue.add(future);

        if (m_blockingQueue.size() == m_blockingQueueCapacity) {
            String msg = "The queue is full! if more data arrives before a place is cleared," +
                    " it will cause information to be lost";
            m_logger.warning(msg);
        }
    }

    //endregion

    //region IExceptionDistributor<Exception>

    @Override
    public void setExceptionHandler(IHandler<Exception> exceptionHandler) {
        m_exceptionHandler = exceptionHandler;
    }

    //endregion

    //endregion

    //region Nested classes

    private class ConsumerThread extends LongRunStoppableThread {

        @Override
        protected boolean internalRun() {

            try {
                Future<?> data = m_blockingQueue.poll(TIME_OUT_MILLISECONDS, TimeUnit.MILLISECONDS);
                if (data != null) {
                    data.get();
                }
            } catch (InterruptedException ex) {
                m_logger.error("Consuming interrupted in IExecutor: " + RunnableSynchronizedExecutor2.this.getId(), ex);
                return false;
            } catch (ExecutionException | CancellationException ex) {
                m_logger.error("An error occurred while trying to execute a runnable in:" + RunnableSynchronizedExecutor2.this.getId(), ex);
            }

            return true;
        }
    }

    //endregion
}

