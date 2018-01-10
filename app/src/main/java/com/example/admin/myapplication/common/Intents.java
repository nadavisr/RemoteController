/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package com.example.admin.myapplication.common;

import java.util.ArrayList;
import java.util.List;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.common.</P>
 * <P></P>
 */

public final class Intents {
    public final static String KEY_VIDEO_CHANNEL_STOPPED_ON_EXCEPTION = "KEY_VIDEO_CHANNEL_STOPPED_ON_EXCEPTION";
    public final static String KEY_VIDEO_CHANNEL_STOPPED = "KEY_VIDEO_CHANNEL_STOPPED";
    public final static String KEY_VIDEO_CHANNEL_STARTED = "KEY_VIDEO_CHANNEL_STARTED";


    public final static List<String> videoIntentsList;
    public final static List<String> allIntentsList;
    public final static String EXTRA_KEY_EXCEPTION_MESSAGE = "EXTRA_KEY_EXCEPTION_MESSAGE";

    static {
        videoIntentsList =new ArrayList<>();
        videoIntentsList.add(KEY_VIDEO_CHANNEL_STOPPED_ON_EXCEPTION);
        videoIntentsList.add(KEY_VIDEO_CHANNEL_STOPPED);
        videoIntentsList.add(KEY_VIDEO_CHANNEL_STARTED);
    }

    static {
        allIntentsList =new ArrayList<>();
        allIntentsList.addAll(videoIntentsList);
    }
}
