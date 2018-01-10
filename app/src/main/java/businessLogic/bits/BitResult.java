/*
 * Created by admin on 01/10/2017
 * Last modified 12:57 01/10/17
 */

package businessLogic.bits;

import businessLogic.bits.interfaces.IBit;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.bits.</P>
 * <P>The result of {@link IBit} check.</P>
 */

public class BitResult {

    //region Fields

    private final IBit m_bit;

    private final String m_message;

    private final BitResultStatus m_bitResultStatus;

    //endregion

    //region Getters

    public BitResult(IBit bit, BitResultStatus bitResultStatus) {
        this(bit, bitResultStatus, "");
    }

    public BitResult(IBit bit, BitResultStatus bitResultStatus, String message) {
        this.m_bit = bit;
        this.m_message = message;
        this.m_bitResultStatus = bitResultStatus;
    }

    /**
     * @return The {@link IBit} whose result.
     */
    public IBit getBit() {
        return m_bit;
    }

    //endregion

    //region Constructors

    /**
     * @return Possible message (can be empty).
     */
    public String getMessage() {
        return m_message;
    }

    /**
     * @return The result of the bit check.
     */
    public BitResultStatus getBitResultStatus() {
        return m_bitResultStatus;
    }

    //endregion


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("A Bit check result: ");
        stringBuilder.append(m_bitResultStatus);

        if (m_message != null && !m_message.isEmpty()) {
            stringBuilder.append(", with a message:");
            stringBuilder.append(m_message);
        }
        stringBuilder.append('.');

        if (m_bit instanceof BaseBit) {
            stringBuilder.append(m_bit.toString());
        } else {
            stringBuilder.append("IBit data: name=");
            stringBuilder.append(m_bit.getName());
            stringBuilder.append(", impact=");
            stringBuilder.append(m_bit.getImpact());
            stringBuilder.append(", importance=");
            stringBuilder.append(m_bit.getImportance());

        }
        stringBuilder.append('.');
        return stringBuilder.toString();
    }
}