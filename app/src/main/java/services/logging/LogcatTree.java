/*
 * Created by admin on 27/09/2017
 * Last modified 18:42 27/09/17
 */

package services.logging;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.logging.</P>
 * <P>Logcat appender for {@link Timber}.</P>
 *
 * @see Timber.DebugTree
 */

class LogcatTree extends Timber.DebugTree {
    private static final int MAX_ROW_LENGTH = 120;

    @Override
    protected String createStackElementTag(StackTraceElement element) {
        String threadDetails = Thread.currentThread().toString();

        String row = "Row=" + element.getLineNumber();

        String stackElementTag = super.createStackElementTag(element);

        return threadDetails + ", " + row + ", " + stackElementTag;
    }

    @Override
    protected void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable t) {

        if (message == null || message.isEmpty()) return;

        if (priority == Log.ASSERT) {
            Log.wtf(tag, message);
        }

        if (message.length() < MAX_ROW_LENGTH) {
            Log.println(priority, tag, message);
            return;
        }

        String patternString = ".{" + MAX_ROW_LENGTH + "}|.+$";
        Pattern p = Pattern.compile(patternString);
        Matcher m = p.matcher(message);
        while (m.find()) {
            Log.println(priority, tag, m.group());
        }
    }
}
