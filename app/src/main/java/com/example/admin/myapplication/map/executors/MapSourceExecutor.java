/*
 * Created by admin on 12/11/2017
 * Last modified 13:13 12/11/17
 */

package com.example.admin.myapplication.map.executors;

import com.example.admin.myapplication.common.Messages;
import com.example.admin.myapplication.configurations.ConfigurationManager;
import com.example.admin.myapplication.configurations.POJO.MapConfiguration;

import businessLogic.common.BgOperationState;
import businessLogic.common.interfaces.IHandler;
import businessLogic.communication.UdpSubscriber;
import businessLogic.communication.interfaces.ISubscriber;
import businessLogic.graph.executors.interfaces.ISourceExecutor;
import businessLogic.serialization.MsgPackSerializer;
import services.common.ServicesBaseBgOperation;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.video.</P>
 * <P>A map graph source, that receive map data from ip and port using udp connection.</P>
 */

public class MapSourceExecutor extends ServicesBaseBgOperation implements ISourceExecutor<Messages.MapMessage> {

    //region Fields

    private IHandler<Messages.MapMessage> m_sourceHandler;

    private IHandler<Exception> m_exceptionHandler;

    private ISubscriber<Messages.MapMessage> m_subscriber;

    //endregion

    //region Constructors

    public MapSourceExecutor(String id) {
        super(id);
    }

    public MapSourceExecutor() {
        super();
    }
    //endregion

    //region Methods

    //region ISourceExecutor<Messages.MapMessage> Implementation

    @Override
    public void setExceptionHandler(IHandler<Exception> exceptionHandler) {
        m_exceptionHandler = exceptionHandler;
    }

    @Override
    public void setSourceHandler(IHandler<Messages.MapMessage> sourceHandler) {
        m_sourceHandler = sourceHandler;
    }

    //endregion

    //region IBackgroundOperation

    @Override
    protected void internalInitialize() throws Exception {
        MsgPackSerializer<Messages.MapMessage> msgPackSerializer = new MsgPackSerializer<>(Messages.MapMessage.class);

        MapConfiguration mapConfiguration = ConfigurationManager.getMapConfiguration();
        if (mapConfiguration == null) {
            String msg = "Get Null from ConfigurationManager.getConnectivityConfiguration(), so it is not possible initialize MapSourceExecutor.";
            throw new RuntimeException(msg);
        }
        short port = mapConfiguration.getPort();

        m_subscriber = new UdpSubscriber<>(m_logger, msgPackSerializer, port);

        m_subscriber.initialize();

        if (m_subscriber.getState() != BgOperationState.Ready) {
            String msg = "Failed on try to initialize ISubscriber<Messages.ImageMessage>, so it is not possible initialize MapSourceExecutor.";
            throw new RuntimeException(msg);
        }

        ExceptionHandler internalExceptionHandler = new ExceptionHandler();

        m_subscriber.setExceptionHandler(internalExceptionHandler);

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

        m_subscriber.subscribe(m_sourceHandler);
        m_subscriber.start();

        if (m_subscriber.getState() != BgOperationState.Running) {
            String msg = "Could not start the video, for more information look up in the log.";
            m_logger.error(msg);
            final Exception ex = new Exception(msg);
            setExceptionToHandler(ex);
        }
        m_logger.info("Video started successfully.");
    }


    @Override
    protected void internalStop() throws Exception {
        if (m_subscriber == null) {
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

    @Override
    protected void innerDispose() throws Exception {
        m_subscriber.dispose();
        m_subscriber = null;
        m_sourceHandler = null;
        m_exceptionHandler = null;
    }

    //endregion

    //region Private Methods

    private void setExceptionToHandler(Exception exceptionToHandler) {
        if (m_exceptionHandler != null) {
            m_exceptionHandler.setInput(exceptionToHandler);
        } else {
            m_logger.warning("Tried to set exception to handler, but the handler not set.", exceptionToHandler);
        }
    }
    //endregion

    //endregion

    //region Nested Classes

    private class ExceptionHandler implements IHandler<Exception> {

        @Override
        public void setInput(Exception exception) {
            if (m_subscriber.getState() == BgOperationState.Running) {
                String msg = "An error has been received from the video subscriber, but the subscriber status is 'Running'.";
                m_logger.warning(msg, exception);
                return;
            }

            String msg = "An error has been received from the video subscriber, trying to stop the executor.";
            m_logger.error(msg, exception);
            stop();
            if (getState() != BgOperationState.Ready) {
                m_logger.error("Failed on try to stop the video subscriber, for more information look at the log.");
            }
            m_bgOperationState = BgOperationState.Error;
            m_logger.error("As a result to received exception the executor status has been updated to 'Error'");

            setExceptionToHandler(exception);
        }
    }


    //endregion
}
