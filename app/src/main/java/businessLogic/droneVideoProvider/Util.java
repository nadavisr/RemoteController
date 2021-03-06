/*
 * Created by Administrator on 02/01/2018
 * Last modified 13:52 28/12/17
 */

package businessLogic.droneVideoProvider;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by Administrator on 28/12/2017.
 */

public class Util
{
        static boolean isDebug(Context context) {
            return (0 != (context.getApplicationContext().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE));
        }

        static void close(InputStream inputStream) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }

        static void close(OutputStream outputStream) {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }

        static String convertInputStreamToString(InputStream inputStream) {
            try {
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                String str;
                StringBuilder sb = new StringBuilder();
                while ((str = r.readLine()) != null) {
                    sb.append(str);
                }
                return sb.toString();
            } catch (IOException e) {
                Log.e("inp stream to str", e.getMessage());
            }
            return null;
        }

        static void destroyProcess(Process process) {
            if (process != null)
                process.destroy();
        }

        static boolean killAsync(AsyncTask asyncTask) {
            return asyncTask != null && !asyncTask.isCancelled() && asyncTask.cancel(true);
        }

        static boolean isProcessCompleted(Process process) {
            try {
                if (process == null) return true;
                process.exitValue();
                return true;
            } catch (IllegalThreadStateException e) {
                // do nothing
            }
            return false;
        }
}
