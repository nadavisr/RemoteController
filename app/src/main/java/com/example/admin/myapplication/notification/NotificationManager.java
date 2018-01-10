/*
 * Created by admin on 05/11/2017
 * Last modified 09:57 05/11/17
 */

package com.example.admin.myapplication.notification;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import businessLogic.common.interfaces.ILog;
import services.logging.LogManager;


public class NotificationManager {

    private final static ILog s_logger = LogManager.getLogger();

    private static Handler s_handler = null;

    public static void showLongToast(final Context context, final String msg) throws NullPointerException {
        if (context == null) {
            String exMsg = "NotificationManager.showLongToast() received null context.";
            s_logger.error(exMsg);
            throw new NullPointerException(exMsg);
        }
        if (msg == null || msg.isEmpty()) {
            return;
        }
        showToast(context, msg, Toast.LENGTH_LONG);
    }

    public static void showShortToast(final Context context, final String msg) throws NullPointerException {
        if (context == null) {
            String exMsg = "NotificationManager.showLongToast() received null context.";
            s_logger.error(exMsg);
            throw new NullPointerException(exMsg);
        }
        if (msg == null || msg.isEmpty()) {
            return;
        }
        showToast(context, msg, Toast.LENGTH_SHORT);
    }

    private static void showToast(final Context context, final String msg, final int toastLength) {

        if(s_handler==null){
            s_handler = new Handler(Looper.getMainLooper());
        }
        s_handler.post(() -> Toast.makeText(context, msg, toastLength).show());
    }
}
