
/*
 * Created by admin on 27/09/2017
 * Last modified 11:42 27/09/17
 */

package services.logging;


import android.annotation.SuppressLint;

import org.slf4j.LoggerFactory;

import businessLogic.common.interfaces.ILog;
import ch.qos.logback.classic.LoggerContext;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.logging.</P>
 * <P>A class which managing singleton instance of {@link ILog}.</P>
 *
 * @see ILog
 */

@SuppressLint("LogNotTimber")
public class LogManager {

    private static ILog s_instance;

    public static ILog getLogger() {
        try {
            if (s_instance == null) {
                synchronized (LogManager.class) {
                    if (s_instance == null) {
                        s_instance = new Log();
                    }
                }
            }
        } catch (Exception ex) {
            s_instance = null;
            android.util.Log.w(LogManager.class.getName(), "Failed on try to create log s_instance", ex);
        }
        return s_instance;
    }

    public static void terminateLogger() {

        try {
            if (s_instance != null) {
                synchronized (LogManager.class) {
                    if (s_instance != null) {
                        s_instance.info("File Log shutted down.");
                        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                        loggerContext.stop();
                    }
                }
            }
        } catch (Exception ex) {
            android.util.Log.e(LogManager.class.getName(), "Failed on try to terminate logger", ex);
        } finally {
            s_instance = null;
        }
    }
}

