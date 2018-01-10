/*
 * Created by admin on 03/10/2017
 * Last modified 07:49 03/10/17
 */

package services.bits;

import android.net.wifi.WifiManager;
import android.support.annotation.IntRange;

import businessLogic.bits.BaseBit;
import businessLogic.bits.BitImpact;
import businessLogic.bits.BitResult;
import businessLogic.bits.BitResultStatus;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: services.bits.</P>
 * <P>Is wifi turned on bit.</P>
 */

public class WifiBit extends BaseBit {

    //region Fields

    private final BitResult m_goodBitResult;
    private final BitResult m_errorBitResult;
    private final WifiManager m_wifiManager;

    //endregion

    //region Constructors

    public WifiBit(WifiManager wifiManager, @IntRange(from = 1, to = 10) int importance, BitImpact impact) {
        super("LocationBit", importance, impact);

        m_wifiManager = wifiManager;

        m_goodBitResult = new BitResult(this, BitResultStatus.OK, "The WIFI is turned on");
        m_errorBitResult = new BitResult(this, BitResultStatus.Error, "The WIFI is turned off");
    }

    //endregion

    //region BaseBit Implementation

    @Override
    protected BitResult innerPerformBit() throws Exception {
        if (m_wifiManager.isWifiEnabled()) {
            return m_goodBitResult;
        } else {
            return m_errorBitResult;
        }
    }

    //endregion
}
