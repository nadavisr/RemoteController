/*
 * Created by admin on 27/09/2017
 * Last modified 18:42 27/09/17
 */

package services.logging;

import android.annotation.SuppressLint;

import com.example.admin.myapplication.BuildConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import businessLogic.common.LogLevel;
import businessLogic.common.interfaces.ILog;
import ch.qos.logback.classic.Level;
import timber.log.Timber;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.logging.</P>
 * <P>A class that implements {@link ILog} using {@link Timber}.</P>
 * <P>The logger write to file using {@link org.slf4j.Logger}
 * and also write to Logcat in debug mode.</P>
 *
 * @see ILog
 */

class Log implements ILog {

    //region Fields

    private LogLevel m_logLevel;

    //endregion

    //region Constructors

    Log() throws Exception{
        initialize(BuildConfig.DEBUG);
    }

    Log(boolean isDebugMode) throws Exception {
        initialize(isDebugMode);
    }

    @SuppressLint("LogNotTimber")
    private void initialize(boolean isDebugMode) throws Exception{
        FileLoggingTree fileLoggingTree;
        try {
            if (isDebugMode) {
                Timber.plant(new LogcatTree());
                fileLoggingTree = new FileLoggingTree(Level.WARN);
                m_logLevel = LogLevel.Verbose;
            } else {
                fileLoggingTree = new FileLoggingTree(Level.INFO);
                m_logLevel = LogLevel.Info;
            }
            Timber.plant(fileLoggingTree);
            info("The log has been initialized at the " + fileLoggingTree.getLoggingLevel() + " level, the number of trees is " + Timber.treeCount());

        } catch (Exception ex) {
            android.util.Log.w(Log.class.getName(), "Exception in log constructor", ex);
            throw ex;
        }
    }
    //endregion

    //region ILog Implementation

    @Override
    public LogLevel getLogLevel() {
        return m_logLevel;
    }

    @Override
    public void write(LogLevel level, String message, Throwable throwable) {
        setTag();

        switch (level) {
            case Verbose:
                verbose(message, throwable);
                break;

            case Debug:
                debug(message, throwable);
                break;

            case Info:
                info(message, throwable);
                break;

            case Warning:
                warning(message, throwable);
                break;

            case Error:
                error(message, throwable);
                break;

            case Fatal:
                fatal(message, throwable);
                break;

            default:
                String msg = "Received message without known level: \"" + message + "\"";
                warning(msg, throwable);
                break;
        }
    }

    @Override
    public void write(LogLevel level, String message) {
        this.write(level, message, null);
    }

    @Override
    public void verbose(String message, Throwable throwable) {
        Timber.v(throwable, message);
    }

    @Override
    public void verbose(String message) {
        setTag();
        this.verbose(message, null);
    }

    @Override
    public void debug(String message, Throwable throwable) {
        setTag();
        Timber.d(throwable, message);
    }

    @Override
    public void debug(String message) {
        this.debug(message, null);
    }

    @Override
    public void info(String message, Throwable throwable) {
        setTag();
        Timber.i(throwable, message);
    }

    @Override
    public void info(String message) {
        this.info(message, null);
    }

    @Override
    public void warning(String message, Throwable throwable) {
        setTag();
        Timber.w(throwable, message);
    }

    @Override
    public void warning(String message) {
        this.warning(message, null);
    }

    @Override
    public void error(String message, Throwable throwable) {
        setTag();
        Timber.e(throwable, message);
    }

    @Override
    public void error(String message) {
        this.error(message, null);
    }

    @Override
    public void fatal(String message, Throwable throwable) {
        setTag();
        Timber.wtf(throwable, message);
    }

    @Override
    public void fatal(String message) {
        this.fatal(message, null);
    }

    //endregion

    //region Methods

    private void setTag() {
        final String logClass = this.getClass().getName();

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        int logClassIndex = -1;

        for (int elementIndex = 0; elementIndex < stackTraceElements.length - 1; elementIndex++) {

            String firstElementClass = stackTraceElements[elementIndex].getClassName();
            String secondElementClass = stackTraceElements[elementIndex + 1].getClassName();

            if (firstElementClass.equals(logClass) && !secondElementClass.equals(logClass)) {
                logClassIndex = elementIndex + 1;
                break;
            }
        }

        if (logClassIndex == -1) {
            int lastElementIndex = stackTraceElements.length - 1;
            String elementClass = stackTraceElements[lastElementIndex].getClassName();
            if (elementClass.equals(logClass)) {
                logClassIndex = lastElementIndex;
            } else {
                return;
            }
        }

        StackTraceElement element = stackTraceElements[logClassIndex];

        String tag = element.getClassName();

        final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");
        Matcher m = ANONYMOUS_CLASS.matcher(tag);

        if (m.find()) {
            tag = m.replaceAll("");
        }
        tag = tag.substring(tag.lastIndexOf('.') + 1);


        if (!tag.equals("")) {
            String threadDetails = Thread.currentThread().toString();
            String row = "Row=" + element.getLineNumber();

            Timber.tag(threadDetails + ", " + row + ", " + tag);
        }
    }

    //endregion
}
