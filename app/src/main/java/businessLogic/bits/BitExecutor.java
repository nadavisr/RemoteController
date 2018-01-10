/*
 * Created by admin on 01/10/2017
 * Last modified 13:16 01/10/17
 */

package businessLogic.bits;

import android.support.annotation.IntRange;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import businessLogic.bits.interfaces.IBit;
import businessLogic.common.interfaces.ILog;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.bits.</P>
 * <P>A class that receives a {@link List} of tests,
 * runs them in cycles in the most parallel manner possible.</P>
 */

public class BitExecutor {

    //region Fields

    private final ILog m_logger;

    private final List<IBit> m_bits;

    private final int m_bitsFrequencyInMillis;

    private final int m_numberOfBits;

    private TimerTask m_bitExecutorTimerTask;

    private Timer m_executingTimer;

    private boolean m_executing;

    //endregion

    //region Getters

    public BitExecutor(ILog logger, List<IBit> bits, @IntRange(from = 1) int bitsFrequencyInMillis) {
        m_logger = logger;
        m_executing = false;
        m_bitsFrequencyInMillis = bitsFrequencyInMillis;
        m_numberOfBits = bits.size();
        m_bits = bits;

        sortBits();

    }

    //endregion

    //region Constructors

    public boolean isExecuting() {
        return m_executing;
    }

    //endregion

    //Methods

    public void performBits() {
        m_logger.info("Starting Perform " + m_numberOfBits + " bits..");
        m_executing = true;
        try {
            m_bitExecutorTimerTask = new BitExecutorTimerTask(m_bits,m_bitsFrequencyInMillis);
            m_executingTimer = new Timer();
            m_executingTimer.schedule(m_bitExecutorTimerTask, 0, m_bitsFrequencyInMillis);
            m_logger.info(m_numberOfBits + " bits are performing in cycles of " + m_bitsFrequencyInMillis
                    + " milliseconds.");
        } catch (Exception ex) {
            m_logger.error("Failed to run IBits performer task.", ex);
            m_executing = false;
        }

    }

    public void stopPerformBits() {
        if (!m_executing) {
            return;
        }
        m_logger.info("Stopping Perform " + m_numberOfBits + " bits..");
        m_bitExecutorTimerTask.cancel();
        m_executingTimer.cancel();
        m_executingTimer.purge();
        m_executing = false;
        m_logger.info(m_numberOfBits + " bits are performing stopped");
    }

    private void sortBits() {
        m_bits.sort((firstBit, secondBit) -> {
            if (firstBit.getImpact() == BitImpact.System &&
                    secondBit.getImpact() == BitImpact.Local) {
                return 1;
            }
            if (firstBit.getImpact() == BitImpact.Local &&
                    secondBit.getImpact() == BitImpact.System) {
                return -1;
            }
            return Integer.compare(firstBit.getImportance(), secondBit.getImportance());
        });
    }

    //endregion

}
