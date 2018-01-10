/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package com.example.admin.myapplication.video;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.example.admin.myapplication.common.Messages.ImageMessage;
import com.example.admin.myapplication.video.VideoService.VideoServiceBinder;

import businessLogic.common.BgOperationState;
import businessLogic.common.interfaces.IHandler;
import businessLogic.graph.executors.interfaces.ISourceExecutor;
import services.common.ServicesBaseBgOperation;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.video</P>
 * <P></P>
 */

public class VideoServiceExecutor extends ServicesBaseBgOperation implements ISourceExecutor<ImageMessage> {

    //region Fields

    private final Context m_context;

    private VideoServiceConnection m_serviceConnection;

    private VideoService m_videoService;

    private boolean m_serviceBound;

    private boolean m_startedBeforeBound;

    private IHandler<ImageMessage> m_sourceHandler;

    private IHandler<Exception> m_exceptionHandler;

    //endregion

    //region Constructors

    public VideoServiceExecutor(Context context, String id) {
        super(id);
        m_context = context;
        m_serviceBound = false;
        m_startedBeforeBound = false;
    }

    public VideoServiceExecutor(Context context) {
        this(context, "");
    }

    //endregion

    //region Methods

    @Override
    public void setExceptionHandler(IHandler<Exception> exceptionHandler) {
        m_exceptionHandler = exceptionHandler;
    }

    @Override
    public void setSourceHandler(IHandler<ImageMessage> sourceHandler) {
        m_sourceHandler = sourceHandler;
    }

    //endregion

    //region IBackgroundOperation

    @Override
    protected void internalInitialize() throws Exception {
        if (m_context == null) {
            String msg = "Context is null.";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }

        m_serviceConnection = new VideoServiceConnection();

        final Intent intent = new Intent(m_context, VideoService.class);

        if (!m_serviceBound) {
            m_context.bindService(intent, m_serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void internalStart() throws Exception {
        if (m_sourceHandler == null) {
            String msg = "IHandler<ImageMessage> is null.";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }
        if (m_exceptionHandler == null) {
            String msg = "IHandler<Exception> is null.";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }

        if (!m_serviceBound) {
            m_startedBeforeBound = true;
            m_logger.info("Start methods runs before the binding to VideoService finished, " +
                    "the initialization will be done independently after the binding.");
            return;
        }

        videoStart();
    }

    private void videoStart() {
        if (!m_videoService.isInitialized()) {
            final ExceptionHandler exceptionHandler = new ExceptionHandler();
            m_videoService.setExceptionHandler(exceptionHandler);
            m_videoService.setFrameHandler(m_sourceHandler);
            m_videoService.initialize();
        }
        m_videoService.startVideo();
    }


    @Override
    protected void internalStop() throws Exception {
        if (m_serviceBound) {
            m_videoService.stopVideo();
        }
    }

    @Override
    protected void innerDispose() throws Exception {
        if (m_serviceBound) {
            m_context.unbindService(m_serviceConnection);
        }
    }

    //endregion

    //region Nested Classes

    private class ExceptionHandler implements IHandler<Exception> {

        @Override
        public void setInput(Exception exception) {
            String msg = "An error has been received from the video service," +
                    "as a result the executor status has been updated to 'Error'";
            m_logger.error(msg, exception);
            m_bgOperationState = BgOperationState.Error;
            m_exceptionHandler.setInput(exception);

        }
    }

    private class VideoServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            m_logger.verbose("in onServiceConnected");

            VideoServiceBinder videoServiceBinder = (VideoServiceBinder) service;
            m_videoService = videoServiceBinder.getService();

            if (m_videoService == null) {
                m_serviceBound = false;
                String msg = "Binding to VideoService failed!";
                m_logger.error(msg);
                Exception ex = new Exception(msg);
                m_exceptionHandler.setInput(ex);
                return;
            }

            m_serviceBound = true;

            if (m_startedBeforeBound) {
                m_logger.info("VideoServiceExecutor started before the binding happen, so runs late start...");

                videoStart();
                m_startedBeforeBound = false;

                m_logger.info("VideoServiceExecutor late start finished successfully.");

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            m_logger.verbose("in onServiceDisconnected");
            m_startedBeforeBound = m_serviceBound = false;
            m_videoService = null;
        }
    }

    //endregion
}

