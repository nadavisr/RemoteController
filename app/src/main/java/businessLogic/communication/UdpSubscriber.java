/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.communication;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import businessLogic.common.BaseBgOperation;
import businessLogic.common.BgOperationState;
import businessLogic.common.LongRunStoppableThread;
import businessLogic.common.interfaces.IHandler;
import businessLogic.common.interfaces.ILog;
import businessLogic.communication.interfaces.ISubscriber;
import businessLogic.serialization.interfaces.ITypedDeserializer;


/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.communication.</P>
 * <P>Class that subscribe messages of certain type from UDP connection.</P>
 *
 * @param <TData> The type of subscribed data.
 * @see ISubscriber
 * @see BaseBgOperation
 */

public class UdpSubscriber<TData> extends BaseBgOperation implements ISubscriber<TData> {

    //region Fields

    //region final static fields
    private static final short KB = 1024;

    private static final int BUFFER_SIZE = 63 * KB;

    private static final int TIME_OUT_MILLISECONDS = 1000;
    //endregion

    //region final fields

    private final String m_ip;

    private final short m_port;

    private final ITypedDeserializer<TData> m_deserializer;

    //endregion

    //region private fields

    private IHandler<TData> m_subscribeHandler;

    private DatagramPacket m_receivePacket;

    private DatagramSocket m_udpSocket;

    private VideoReceivingThread m_videoThread;

    private InetAddress m_serverAddress;

    private IHandler<Exception> m_exceptionHandler;

    //endregion

    //endregion

    //region Constructors
    public UdpSubscriber(@NonNull ILog logger, ITypedDeserializer<TData> deserializer, short port) {
        this(logger, deserializer, port, "");
    }

    public UdpSubscriber(@NonNull ILog logger, ITypedDeserializer<TData> deserializer, short port, String id) {
        super(logger, id);
        m_ip = null;
        m_port = port;
        m_deserializer = deserializer;
    }

    public UdpSubscriber(@NonNull ILog logger, ITypedDeserializer<TData> deserializer, String ip, short port) {
        this(logger, deserializer, ip, port, "");
    }

    public UdpSubscriber(@NonNull ILog logger, ITypedDeserializer<TData> deserializer, String ip, short port, String id) {
        super(logger, id);
        m_ip = ip;
        m_port = port;
        m_deserializer = deserializer;
    }
    //endregion

    //region Methods

    private void setExceptionToHandler(Exception exceptionToHandler) {
        if (m_exceptionHandler != null) {
            m_exceptionHandler.setInput(exceptionToHandler);
        }
    }

    //endregion

    //region ISubscriber<TData> implementation
    @Override
    public void subscribe(IHandler<TData> subscribeHandler) {
        m_subscribeHandler = subscribeHandler;
    }

    @Override
    public void unsubscribe() {
        m_subscribeHandler = null;
    }

    @Override
    public void setExceptionHandler(IHandler<Exception> exceptionHandler) {
        m_exceptionHandler = exceptionHandler;
        if (m_exceptionHandler != null) {
            m_logger.info("exception handler initialized.");
        }
    }

    //endregion

    //region BaseBgOperation implementation
    @Override
    protected void internalInitialize() {
        if (m_deserializer == null) {
            String msg = "ITypedDeserializer<TData> is null.";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }

        if (m_ip != null && m_ip.isEmpty()) {
            String msg = "IP string is empty";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }

        if (m_ip != null) {
            try {
                m_serverAddress = InetAddress.getByName(m_ip);
            } catch (UnknownHostException ex) {
                String msg = "Failure on trying to create an IP address";
                m_logger.error(msg, ex);
                throw new RuntimeException(msg, ex);
            }
        } else {
            m_serverAddress = null;
        }
        byte[] m_receiveData = new byte[BUFFER_SIZE];
        m_receivePacket = new DatagramPacket(m_receiveData, m_receiveData.length);
        m_videoThread = new VideoReceivingThread();

    }

    @Override
    protected void internalStart() throws Exception {
        if (m_subscribeHandler == null) {
            String msg = "ISubscribeHandler<TData> is null.";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }
        if (m_exceptionHandler == null) {
            m_logger.warning("UdpSubscriber<TData> starts without initialization of exception handler.");
        }
        try {
            if (m_serverAddress == null) {
                m_udpSocket = new DatagramSocket(m_port);
            } else {
                m_udpSocket = new DatagramSocket(m_port, m_serverAddress);
            }
            m_udpSocket.setSoTimeout(TIME_OUT_MILLISECONDS);
            m_videoThread.start();
        } catch (SocketException e) {
            String msg = "Failed on try to create DatagramSocket on ip=" + m_ip + ", port=" + m_port +
                    ", with timeout: " + TIME_OUT_MILLISECONDS;
            m_logger.error(msg, e);
            throw new Exception(msg, e);
        }
    }

    @Override
    protected void internalStop() {
        m_videoThread.stopRun();
        try {
            m_videoThread.join(TIME_OUT_MILLISECONDS + TIME_OUT_MILLISECONDS / 2);
        } catch (InterruptedException e) {
            String msg = "Failed to attempt to safely stop the subscription thread, interrupting the thread.";
            m_logger.warning(msg, e);
            m_videoThread.interrupt();
        }
        m_udpSocket.disconnect();
        m_udpSocket.close();
    }

    @Override
    protected void innerDispose() throws Exception {
        m_receivePacket = null;
        m_udpSocket = null;
        m_videoThread = null;
    }

    //endregion

    //region Nested classes
    private class VideoReceivingThread extends LongRunStoppableThread {

        private int m_count;

        @Override
        protected boolean internalRun() {

            try {
                   m_udpSocket.receive(m_receivePacket);
            } catch (SocketTimeoutException ex) {
                m_count++;
                if (m_count == 100) {
                    final String msg = "The socket was empty for " + m_count * TIME_OUT_MILLISECONDS +
                            " milliseconds,in ISubscriber: " + UdpSubscriber.this.getId();
                    m_logger.verbose(msg);
                    m_count = 0;
                }
                return true;
            } catch (IOException ex) {
                String msg = "Received exception on try to receive message, subscriber: " + UdpSubscriber.this.getId() + " state update to Error.";
                m_logger.error(msg, ex);
                m_bgOperationState = BgOperationState.Error;
                setExceptionToHandler(ex);
                return false;
            }
            m_count = 0;
            if (m_subscribeHandler == null) {
                return false;
            }
            try {
                TData deserializedData = m_deserializer.deserialize(m_receivePacket.getData());
                m_subscribeHandler.setInput(deserializedData);
            } catch (IOException ex) {
                m_logger.verbose("A message was received that could not be deserialized,in ISubscriber: " + UdpSubscriber.this.getId(), ex);
                setExceptionToHandler(ex);
            }
            return true;
        }
    }
    //endregion
}


