/*
 * Created by admin on 22/10/2017
 * Last modified 13:51 22/10/17
 */

package businessLogic.common.exceptions;

import businessLogic.common.interfaces.IBackgroundOperation;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.common.exceptions.</P>
 * <P></P>
 */

public class NotRunningBackgroundOperationException extends RuntimeException {

    public <T extends IBackgroundOperation> NotRunningBackgroundOperationException(T bgOperation) {
        super("An attempt was made to use the IBackgroundOperation: " + bgOperation.getId() +
                ", while the state is: " + bgOperation.getState());
    }

    public <T extends IBackgroundOperation> NotRunningBackgroundOperationException(T bgOperation, String msg) {
        super("The BackgroundOperation: " + bgOperation.getId()
                + ", received input while it was in the state: " + bgOperation.getState()
                + ". Additional message: " + msg);
    }
}
