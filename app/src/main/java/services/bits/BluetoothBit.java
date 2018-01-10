/*
 * Created by admin on 03/10/2017
 * Last modified 09:19 03/10/17
 */

package services.bits;

import android.bluetooth.BluetoothAdapter;
import android.support.annotation.IntRange;

import businessLogic.bits.BaseBit;
import businessLogic.bits.BitImpact;
import businessLogic.bits.BitResult;
import businessLogic.bits.BitResultStatus;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: services.bits.</P>
 * <P>Is bluetooth turned on bit.</P>
 */

public class BluetoothBit extends BaseBit {

    private final BitResult m_goodBitResult;
    private final BitResult m_errorBitResult;
    private final BluetoothAdapter m_bluetoothAdapter;

    public BluetoothBit(BluetoothAdapter bluetoothAdapter, @IntRange(from = 1, to = 10) int importance, BitImpact impact) {
        super("LocationBit", importance, impact);

        m_bluetoothAdapter = bluetoothAdapter;

        m_goodBitResult = new BitResult(this, BitResultStatus.OK, "The Bluetooth is turned on");
        m_errorBitResult = new BitResult(this, BitResultStatus.Error, "The Bluetooth is turned off");
    }

    @Override

    protected BitResult innerPerformBit() throws Exception {

        if (m_bluetoothAdapter.isEnabled()) {
            return m_goodBitResult;
        } else {
            return m_errorBitResult;
        }
    }
}
