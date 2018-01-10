/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.common.interfaces;

import businessLogic.common.LogLevel;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.common.interfaces.</P>
 * <P>The interface defines log capabilities.</P>
 */

public interface ILog {

    LogLevel getLogLevel();

    void write(LogLevel level, String message, Throwable throwable);

    void write(LogLevel level, String message);

    void verbose(String message, Throwable throwable);

    void verbose(String message);

    void debug(String message, Throwable throwable);

    void debug(String message);

    void info(String message, Throwable throwable);

    void info(String message);

    void warning(String message, Throwable throwable);

    void warning(String message);

    void error(String message, Throwable throwable);

    void error(String message);

    void fatal(String message, Throwable throwable);

    void fatal(String message);
}
