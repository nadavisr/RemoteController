/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.common;

import businessLogic.common.interfaces.ILog;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package:  businessLogic.common.</P>
 * <P>Possible writing levels of {@link ILog}.</P>
 */

public enum LogLevel {
    Verbose,
    Debug,
    Info,
    Warning,
    Error,
    Fatal
}
