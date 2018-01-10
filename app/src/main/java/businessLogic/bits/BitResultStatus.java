/*
 * Created by admin on 01/10/2017
 * Last modified 12:59 01/10/17
 */

package businessLogic.bits;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.bits.</P>
 * <P>The possible statuses of {@link businessLogic.bits.interfaces.IBit} result.</P>
 */

public enum BitResultStatus {
    OK,
    Warning,
    Error;


    @Override
    public String toString() {
        switch (this) {
            case OK:
                return "OK";
            case Error:
                return "Error";
            case Warning:
                return "Warning";
        }
        return "Unknown State";
    }
}
