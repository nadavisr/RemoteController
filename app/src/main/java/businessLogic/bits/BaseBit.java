/*
 * Created by admin on 01/10/2017
 * Last modified 12:53 01/10/17
 */

package businessLogic.bits;

import android.support.annotation.IntRange;

import java.util.ArrayList;
import java.util.Collection;

import businessLogic.bits.interfaces.IBit;
import businessLogic.common.interfaces.IHandler;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.bits.</P>
 * <P>Defines a base for {@link IBit}.</P>
 *
 * @see IHandler
 */

public abstract class BaseBit implements IBit {

    //region Fields

    private final String m_name;
    private final int m_importance;
    private final BitImpact m_impact;

    private Collection<IHandler<BitResult>> m_bitResultHandler;

    //endregion

    //region Constructors

    public BaseBit(String name, @IntRange(from = 1, to = 10) int importance, BitImpact impact) {
        m_name = name;
        m_importance = importance;
        m_impact = impact;
        m_bitResultHandler=new ArrayList<>();
    }

    //endregion

    //region IBit Implementation

    @Override
    public String getName() {
        return m_name;
    }

    @Override
    public int getImportance() {
        return m_importance;
    }


    @Override
    public BitImpact getImpact() {
        return m_impact;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public void setBitResultHandlers(IHandler<BitResult>... bitResultHandlers) {
        IHandler<BitResult>[] bitResultHandlersArray = bitResultHandlers;

        if(bitResultHandlersArray==null){
            return;
        }

        for (IHandler<BitResult> bitResultHandler : bitResultHandlersArray) {
            if(bitResultHandler!=null){
                m_bitResultHandler.add(bitResultHandler);
            }
        }
    }

    @Override
    public void performBit() throws Exception {
        BitResult bitResult = innerPerformBit();
        if (m_bitResultHandler == null || bitResult == null || m_bitResultHandler.isEmpty()) {
            return;
        }

        for (IHandler<BitResult> bitHandler : m_bitResultHandler) {
            if (bitHandler != null) {
                bitHandler.setInput(bitResult);
            }
        }
    }

    @Override
    public void clearHandlers() {
        m_bitResultHandler.clear();
    }

    //endregion

    //region Object Overrides

    @Override
    public String toString() {
        return "IBit data: name=" + m_name + ", impact="
                + m_impact + ", importance=" + m_importance;
    }

    //endregion

    //region Abstract Methods

    /**
     * Implements the actual perform logic in inheriting bits.
     *
     * @throws Exception if the perform failed.
     */
    protected abstract BitResult innerPerformBit() throws Exception;

    //endregion
}
