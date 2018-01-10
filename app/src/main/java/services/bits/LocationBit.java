/*
 * Created by admin on 02/10/2017
 * Last modified 16:07 02/10/17
 */

package services.bits;

import android.location.LocationManager;
import android.support.annotation.IntRange;

import businessLogic.bits.BaseBit;
import businessLogic.bits.BitImpact;
import businessLogic.bits.BitResult;
import businessLogic.bits.BitResultStatus;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: services.bits.</P>
 * <P>Is location turned on bit.</P>
 */

public class LocationBit extends BaseBit {


    private final BitResult m_goodBitResult;
    private final BitResult m_errorBitResult;
    private final LocationManager m_locationManager;

    public LocationBit(LocationManager locationManager, @IntRange(from = 1, to = 10) int importance, BitImpact impact) {
        super("LocationBit", importance, impact);

        m_locationManager = locationManager;

        m_goodBitResult = new BitResult(this, BitResultStatus.OK, "The location is turned On");
        m_errorBitResult = new BitResult(this, BitResultStatus.Error, "The location is turned off");

    }

    @Override

    protected BitResult innerPerformBit() throws Exception {

        if (m_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return m_goodBitResult;
        } else {
            return m_errorBitResult;
        }
    }
}
