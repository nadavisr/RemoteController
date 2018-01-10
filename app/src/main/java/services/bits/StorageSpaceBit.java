/*
 * Created by admin on 01/10/2017
 * Last modified 14:49 01/10/17
 */

/*
 * Created by admin on 01/10/2017
 * Last modified 14:30 01/10/17
 */

package services.bits;

import android.os.StatFs;
import android.support.annotation.IntRange;

import businessLogic.bits.BaseBit;
import businessLogic.bits.BitImpact;
import businessLogic.bits.BitResult;
import businessLogic.bits.BitResultStatus;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: services.</P>
 * <P>Storage empty space bit check.</P>
 */

public class StorageSpaceBit extends BaseBit {


    //region Fields
    private final String m_externalStoragePath;

    //endregion

    //region Constructors
    public StorageSpaceBit(String path, String name, @IntRange(from = 1, to = 10) int importance, BitImpact impact) {
        super(name, importance, impact);
        m_externalStoragePath = path;

    }
    //endregion

    //region IBit Implementation

    @Override
    protected BitResult innerPerformBit() throws Exception {

        StatFs statFs = new StatFs(m_externalStoragePath);
        double blockCountLong = (double)statFs.getBlockCountLong();
        double availableBytes = (double)statFs.getAvailableBlocksLong();

        int percent = 100 - (int) ((availableBytes / blockCountLong) * 100 + 0.5);

        if (percent < 80) {
            return new BitResult(this, BitResultStatus.OK);
        }

        String msg = "External memory is in use of over " + percent + "%.";

        if (percent <  95) {
            return new BitResult(this, BitResultStatus.Warning, msg);
        }

        return new BitResult(this, BitResultStatus.Error, msg);
    }

    //endregion
}
