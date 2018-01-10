/*
 * Created by admin on 27/09/2017
 * Last modified 18:42 27/09/17
 */

package services.logging;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.admin.myapplication.MainApplication;

import org.slf4j.LoggerFactory;

import java.util.TimeZone;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.html.HTMLLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.StatusPrinter;
import timber.log.Timber;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.logging.</P>
 * <P>File appender for {@link Timber}, using {@link org.slf4j.Logger}.</P>
 *
 * @see Timber.DebugTree
 */

class FileLoggingTree extends Timber.DebugTree {

    //region Fields

    //region Static

    private static final Level LOGGING_DEFAULT_LEVEL = Level.INFO;

    private static final String LOG_PREFIX = "log";

    //endregion

    private static final org.slf4j.Logger m_logger = LoggerFactory.getLogger(FileLoggingTree.class);
    private final String m_logPath;
    private final String m_logName;
    private final Level m_loggingLevel;
    //endregion

    //region Constructors

    FileLoggingTree() {
        m_logPath = MainApplication.getApplicationExternalFolderPath() + "/logs";
        m_logName = "";
        m_loggingLevel = LOGGING_DEFAULT_LEVEL;
        configureLogger();
    }

    FileLoggingTree(Level level) {
        m_logPath = MainApplication.getApplicationExternalFolderPath() + "/logs";
        m_logName = "";
        m_loggingLevel = (level != null && level.toInt() >= Level.TRACE_INT && level.toInt() <= Level.ERROR_INT) ?
                level : LOGGING_DEFAULT_LEVEL;
        configureLogger();
    }

    //endregion

    //region Getters

    public Level getLoggingLevel() {
        return m_loggingLevel;
    }

    //endregion

    //region Methods

    private void configureLogger() {
        // reset the default context (which may already have been initialized)
        // since we want to reconfigure it
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();

        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
        rollingFileAppender.setContext(loggerContext);
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setLazy(true);

        rollingFileAppender.setFile(m_logPath + "/" + LOG_PREFIX + m_logName + ".html");
        SizeAndTimeBasedFNATP<ILoggingEvent> fileNamingPolicy = new SizeAndTimeBasedFNATP<>();
        fileNamingPolicy.setContext(loggerContext);
        fileNamingPolicy.setMaxFileSize("20MB");

        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setFileNamePattern(m_logPath + "/" + LOG_PREFIX + ".%d{yyyy-MM-dd}.%i.html");
        rollingPolicy.setMaxHistory(7);
        rollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(fileNamingPolicy);
        rollingPolicy.setParent(rollingFileAppender);  // parent and context required!
        rollingPolicy.start();

        String timezoneID = TimeZone.getDefault().getID();
        String pattern = "%date{dd MMM yyyy;HH:mm:ss, "+timezoneID+"}%level%thread%msg";
        HTMLLayout htmlLayout = new HTMLLayout();
        htmlLayout.setContext(loggerContext);

        htmlLayout.setPattern(pattern);
        htmlLayout.start();

        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setContext(loggerContext);
        encoder.setLayout(htmlLayout);
        encoder.start();

        /* Alternative text encoder - very clean pattern, takes up less space

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setCharset(Charset.forName("UTF-8"));
        encoder.setPattern("%date %level [%thread] %msg%n");
        encoder.start();
        */

        rollingFileAppender.setRollingPolicy(rollingPolicy);
        rollingFileAppender.setEncoder(encoder);
        rollingFileAppender.start();

        // add the newly created appenders to the root logger;
        // qualify LogManager to disambiguate from org.slf4j.LogManager
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(m_loggingLevel);
        root.addAppender(rollingFileAppender);

        // print any status messages (warnings, etc) encountered in logback config
        StatusPrinter.print(loggerContext);

    }

    @Override
    protected void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable throwable) {
        if (message == null || message.isEmpty()) return;

        StringBuilder stringBuilder = new StringBuilder();
        if (tag != null) {
            stringBuilder.append(tag);
            stringBuilder.append(":\n");
        }

        stringBuilder.append("Message: ");
        stringBuilder.append(message);

        if (throwable != null) {
            stringBuilder.append("\"\nThrowable: ");
            stringBuilder.append(throwable);
        }
        else{
            stringBuilder.append("\".");
        }
        String logMessage = stringBuilder.toString();

        switch (priority) {
            case Log.VERBOSE:
                m_logger.trace(logMessage);
                break;
            case Log.DEBUG:
                m_logger.debug(logMessage);
            case Log.INFO:
                m_logger.info(logMessage);
                break;
            case Log.WARN:
                m_logger.warn(logMessage);
                break;
            case Log.ERROR:
                m_logger.error(logMessage);
                break;
            case Log.ASSERT:
                m_logger.error(logMessage);
                break;
        }
    }

    //endregion
}
