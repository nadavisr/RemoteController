/*
 * Created by admin on 01/10/2017
 * Last modified 17:02 01/10/17
 */

package services.bits;

import java.util.Objects;

import businessLogic.bits.BaseBit;
import businessLogic.bits.BitResult;
import businessLogic.bits.BitResultStatus;
import businessLogic.bits.interfaces.IBit;
import businessLogic.common.interfaces.IHandler;
import businessLogic.common.interfaces.ILog;
import services.logging.LogManager;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: services.bits.</P>
 * <P>{@link IHandler} of {@link BitResult} that print if necessary to {@link ILog}.</P>
 */

public class BitResultLogHandler implements IHandler<BitResult> {
    //region Fields

    private final ILog m_log;

    //endregion

    //region Constructors

    public BitResultLogHandler() {
        m_log = LogManager.getLogger();
    }

    public BitResultLogHandler(ILog log) {
        m_log = log;
    }

    //endregion

    //region IHandler<BitResult> implementation

    @Override
    public void setInput(BitResult bitResult) {
        if (bitResult.getBitResultStatus() == BitResultStatus.OK) {
            return;
        }

        String logMessage = "IBit received with " + bitResult.getBitResultStatus().toString() + " status, ";

        String bitMessage = bitResult.getMessage();
        if (bitMessage != null && !Objects.equals(bitMessage, "")) {
            logMessage += "and Message:\"" + bitMessage + "\".";
        }

        IBit bit = bitResult.getBit();
        if (bit instanceof BaseBit) {
            logMessage += bit.toString();
        } else {
            logMessage = "IBit data: name=" + bit.getName() + ", impact="
                    + bit.getImpact() + ", importance=" + bit.getImportance();
        }

        if (bitResult.getBitResultStatus() == BitResultStatus.Warning) {
            m_log.warning(logMessage);
            return;
        }

        m_log.error(logMessage);
    }

    //endregion
}
