/*
 * Created by admin on 01/10/2017
 * Last modified 12:53 01/10/17
 */

package businessLogic.bits.interfaces;

import android.support.annotation.IntRange;

import businessLogic.bits.BitImpact;
import businessLogic.bits.BitResult;
import businessLogic.common.interfaces.IHandler;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.bits.interfaces.</P>
 * <P>The interface defines a bit checker.</P>
 */

public interface IBit {

    /**
     * @return {@link IBit} name.
     */
    String getName();

    /**
     * @return {@link IBit} importance, 1 is the lowest and 10 is the highest.
     */
    @IntRange(from = 1, to = 10)
    int getImportance();

    /**
     * @return If the bit impacts only locally or systematically.
     */
    BitImpact getImpact();

    /**
     * Add a group of {@link IHandler} of bit check result.
     *
     * @param bitResultHandlers A group of {@link IHandler} to the bit check result.
     */
    void setBitResultHandlers(IHandler<BitResult>... bitResultHandlers);

    /**
     * Clear all the collection of {@link IHandler}.
     */
    void clearHandlers();


    /**
     * Start perform bit check.
     */
    void performBit() throws Exception;

}
