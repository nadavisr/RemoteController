/*
 * Created by admin on  27/09/2017
 * Last modified 19:17 26/09/17
 */

package com.example.admin.myapplication.video;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.admin.myapplication.common.Messages.ImageMessage;
import com.example.admin.myapplication.configurations.ConfigurationManager;
import com.example.admin.myapplication.configurations.POJO.VideoConfiguration;

import businessLogic.common.BgOperationState;
import businessLogic.common.exceptions.NotInitializedException;
import businessLogic.common.interfaces.IHandler;
import businessLogic.common.interfaces.IInitializable;
import businessLogic.common.interfaces.ILog;
import businessLogic.communication.UdpSubscriber;
import businessLogic.communication.interfaces.ISubscriber;
import businessLogic.serialization.MsgPackSerializer;
import services.logging.LogManager;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.video</P>
 * <P></P>
 */

@SuppressLint("Registered")
public class VideoService extends Service implements IInitializable {

    //region Fields

    private IBinder m_videoServiceBinder;

    private ILog m_logger;

    private IHandler<ImageMessage> m_frameHandler;

    private IHandler<Exception> m_exceptionHandler;

    private IHandler<Exception> m_internalExceptionHandler;

    private ISubscriber<ImageMessage> m_subscriber;

    private boolean m_initialized;

    //endregion

    //region Constructors

    public VideoService() {
    }

    //endregion

    //region Setters

    public void setFrameHandler(IHandler<ImageMessage> frameHandler) {
        m_frameHandler = frameHandler;
    }

    public void setExceptionHandler(IHandler<Exception> exceptionHandler) {
        m_exceptionHandler = exceptionHandler;
        if (m_exceptionHandler != null) {
            m_logger.info("exception handler initialized.");
        }
    }

    //endregion

    //region Methods

    //region Service Lifecycle Methods

    @Override
    public IBinder onBind(Intent intent) {
        m_logger.verbose("in onBind.");
        return m_videoServiceBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        m_logger.verbose("in onRebind.");

        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        m_logger.verbose("in onUnbind.");
        stopVideo();
        return true;
    }

    @Override
    public void onCreate() {
        m_initialized = false;

        m_logger = LogManager.getLogger();
        m_logger.verbose("in onCreate.");

        m_videoServiceBinder = new VideoServiceBinder();

        initializeConnectivity();

        m_internalExceptionHandler = new ExceptionHandler();

        m_frameHandler = null;
        m_exceptionHandler = null;

        super.onCreate();
    }


    @Override
    public void onDestroy() {
        m_logger.verbose("in onDestroy.");
        m_initialized = false;

        m_videoServiceBinder = null;
        m_logger = null;
        m_frameHandler = null;
        m_exceptionHandler = null;
        m_subscriber = null;

        super.onDestroy();
    }

    //endregion

    //region Private Methods

    private void initializeConnectivity() {

        MsgPackSerializer<ImageMessage> msgPackSerializer = new MsgPackSerializer<>(ImageMessage.class);

        VideoConfiguration videoConfiguration= ConfigurationManager.getVideoConfiguration();
        if (videoConfiguration == null) {
            throw new RuntimeException("ConnectivityConfiguration not initialized in ConfigurationManager");
        }
        short port = videoConfiguration.getPort();

        m_subscriber = new UdpSubscriber<>(m_logger, msgPackSerializer, port);

    }

    private void setExceptionToHandler(Exception exceptionToHandler) {
        if (m_exceptionHandler != null) {
            m_exceptionHandler.setInput(exceptionToHandler);
        } else {
            m_logger.verbose("exception handler was not set.", exceptionToHandler);
        }
    }

    //endregion

    //region Public Methods

    /**
     * Start listening to video stream.
     *
     * @throws NotInitializedException If the method called before {@link VideoService#initialize()} method.
     */
    public void startVideo() throws NotInitializedException {
        if (!m_initialized) {
            throw new NotInitializedException(VideoService.class);
        }
        m_subscriber.setExceptionHandler(m_internalExceptionHandler);
        m_subscriber.subscribe(m_frameHandler);
        m_subscriber.start();
        if (m_subscriber.getState() != BgOperationState.Running) {
            String msg = "Could not start the video, for more information look up in the log.";
            m_logger.error(msg);
            final Exception ex = new Exception(msg);
            setExceptionToHandler(ex);
        }
        m_logger.info("Video started successfully.");
    }

    /**
     * Stop listening to video stream.
     */
    public void stopVideo() {
        if(m_subscriber==null){
            return;
        }

        try {
            m_subscriber.stop();
            if (m_subscriber.getState() == BgOperationState.Error) {
                String msg = "Could not stop the video, for more information look up in the log.";
                final Exception ex = new Exception(msg);
                setExceptionToHandler(ex);
            }
            m_logger.info("Video stopped successfully.");

        } finally {
            m_subscriber.unsubscribe();
        }
    }

    //endregion

    //region IInitializable implementation
    @Override
    public void initialize() {
        m_subscriber.initialize();
        if(m_subscriber.getState()!= BgOperationState.Ready){
            m_initialized=false;
            return;
        }

        if (m_frameHandler == null) {
            String msg = "IHandler<ImageMessage> is null.";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }
        if (m_exceptionHandler == null) {
            m_logger.warning("Video starts without initialization of exception handler.");
        }

        m_initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return m_initialized;
    }

    //endregion

    //endregion

    //region Nested Classes
    class VideoServiceBinder extends Binder {
        VideoService getService() {
            return VideoService.this;
        }
    }

    private class ExceptionHandler implements IHandler<Exception> {

        @Override
        public void setInput(Exception exception) {
            String msg = "An error has been received from the ISubscriber<ImageMessage>, ";
            switch (m_subscriber.getState()) {
                case Running:
                    msg += "as a result nothing has not been changed in the service.";
                    m_logger.warning(msg, exception);
                    break;
                case Ready:
                    msg += "as a result the service trying to restart the video streaming.";
                    m_logger.error(msg, exception);
                    startVideo();
                    break;
                case Error:
                    msg += "as a result the stopping the video streaming.";
                    m_logger.error(msg, exception);
                    stopVideo();
                    setExceptionToHandler(exception);
                    break;
            }
        }
    }
    //endregion
}

