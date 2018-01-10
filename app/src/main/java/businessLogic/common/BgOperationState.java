/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.common;

import businessLogic.common.interfaces.IBackgroundOperation;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package:  businessLogic.common.</P>
 * <P>Possible states of {@link IBackgroundOperation}.</P>
 */

public enum BgOperationState {
    Error,
    NotReady,
    Ready,
    Running

}
