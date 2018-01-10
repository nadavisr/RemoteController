/*
 * Created by admin on 03/10/2017
 * Last modified 13:38 03/10/17
 */

package businessLogic.bits;

import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import businessLogic.bits.interfaces.IBit;
import businessLogic.common.interfaces.ILog;
import services.logging.LogManager;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.bits.</P>
 * <P>{@link TimerTask} that execute all bits check.</P>
 */
class BitExecutorTimerTask extends TimerTask {

    //region Fields

    private final ILog m_logger;
    private final ExecutorService m_executorService;
    private final Iterable<IBit> m_bits;
    private final int m_bitsFrequencyInMillis;

    //endregion

    //region Constructors

    BitExecutorTimerTask(Iterable<IBit> bits, int bitsFrequencyInMillis) {
        m_bits = bits;
        m_bitsFrequencyInMillis = bitsFrequencyInMillis;
        int numOfCores = Runtime.getRuntime().availableProcessors();
        m_logger = LogManager.getLogger();
        m_executorService = Executors.newFixedThreadPool(numOfCores);
    }

    //endregion

    //region TimerTask Implementation

    @Override
    public void run() {
        final int timeoutInMillis = (int) (0.5 * m_bitsFrequencyInMillis);

        try {
            m_executorService.awaitTermination(timeoutInMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            String msg = "ExecutorService in BitExecutor.BitExecutorTimerTask get " +
                    "InterruptedException while waiting to termination, waiting time: " + timeoutInMillis;
            m_logger.warning(msg, e);
            return;
        }

        for (final IBit bit : m_bits) {
            m_executorService.submit(() -> {
                try {
                    bit.performBit();
                } catch (Exception ex) {

                    String bitData;
                    if (bit instanceof BaseBit) {
                        bitData = bit.toString();
                    } else {
                        bitData = "IBit data: name=" + bit.getName() + ", impact="
                                + bit.getImpact() + ", importance=" + bit.getImportance();
                    }
                    m_logger.error("Bit perform failed! " + bitData, ex);
                }
            });
        }
    }

    //endregion
}
