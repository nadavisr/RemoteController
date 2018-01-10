/*
 * Created by admin on  27/09/2017
 * Last modified 09:40 27/09/17
 */

package businessLogic.communication;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import businessLogic.common.BaseBgOperation;
import businessLogic.common.BgOperationState;
import businessLogic.common.exceptions.NotRunningBackgroundOperationException;
import businessLogic.common.interfaces.ILog;
import businessLogic.communication.interfaces.IPublisher;
import businessLogic.serialization.interfaces.ITypedSerializer;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.communication.</P>
 * <P>Class that publish messages of certain type over UDP connection.</P>
 *
 * @param <TData> The type of published data.
 * @see IPublisher
 * @see BaseBgOperation
 */

public class UdpPublisher<TData> extends BaseBgOperation implements IPublisher<TData> {

    //region Fields

    //region final static fields
    private static final short KB = 1024;

    private static final int BUFFER_SIZE = 63 * KB;

    private static final int TIME_OUT_MILLISECONDS = 1000;
    //endregion

    //region final fields
    private final String m_ip;

    private final short m_port;

    private final ITypedSerializer<TData> m_serializer;
    //endregion

    //region private fields

    private DatagramSocket m_udpSocket;

    private InetAddress m_serverAddress;

    private DatagramPacket m_datagramPacket;

    //endregion

    //endregion

    //region Constructors
    public UdpPublisher(@NonNull ILog logger, ITypedSerializer<TData> deserializer, short port) {
        this(logger, deserializer, port, "");
    }

    public UdpPublisher(@NonNull ILog logger, ITypedSerializer<TData> deserializer, short port, String id) {
        super(logger, id);
        m_ip = null;
        m_port = port;
        m_serializer = deserializer;
    }

    public UdpPublisher(@NonNull ILog logger, ITypedSerializer<TData> deserializer, String ip, short port) {
        this(logger, deserializer, ip, port, "");
    }

    public UdpPublisher(@NonNull ILog logger, ITypedSerializer<TData> deserializer, String ip, short port, String id) {
        super(logger, id);
        m_ip = ip;
        m_port = port;
        m_serializer = deserializer;
    }
    //endregion

    //region IPublisher<TData> implementation
    @Override
    public void publish(TData data) throws IOException , NotRunningBackgroundOperationException {

        if (m_bgOperationState != BgOperationState.Running) {
            NotRunningBackgroundOperationException ex = new NotRunningBackgroundOperationException(this);
            m_logger.error(ex.getMessage());
            throw ex;
        }

        if (!m_udpSocket.isConnected()) {
            String msg = "Failed on try to publish a message, because the socket is disconnected.";
            m_logger.error(msg);
            throw new IOException(msg);
        }

        byte[] bytes;
        try {
            bytes = m_serializer.serialize(data);
        } catch (IOException e) {
            String msg = "Failed on try to serialize the data object";
            m_logger.warning(msg, e);
            throw e;
        }

        m_datagramPacket.setData(bytes);
        try {
            m_udpSocket.send(m_datagramPacket);
        } catch (IOException e) {
            String msg = "Failed on try to publish a message";
            m_logger.warning(msg, e);
            throw e;
        }
    }
    //endregion

    //region BaseBgOperation implementation
    @Override
    protected void internalInitialize() throws Exception {

        if (m_serializer == null) {
            String msg = "ITypedSerializer<TData> is null.";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }

        if (m_ip == null || m_ip.isEmpty()) {
            String msg = "IP string is null or empty";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }

        try {
            m_serverAddress = InetAddress.getByName(m_ip);
        } catch (UnknownHostException ex) {
            String msg = "Failure on trying to create an IP address";
            m_logger.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
        byte[] m_receiveData = new byte[BUFFER_SIZE];
        m_datagramPacket = new DatagramPacket(m_receiveData,  m_receiveData.length);
    }


    @Override
    protected void internalStart() {
        try {
            m_udpSocket = new DatagramSocket();
            m_udpSocket.connect(m_serverAddress,m_port);
        } catch (SocketException e) {
            String msg = "Failed on try to create DatagramSocket with the stats: ip=" + m_ip +
                    " port=" + m_port + ", timeout=" + TIME_OUT_MILLISECONDS;
            m_logger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    @Override
    protected void internalStop() {
        m_udpSocket.disconnect();
        m_udpSocket.close();
    }

    @Override
    protected void innerDispose() throws Exception {
        m_datagramPacket = null;
        m_udpSocket = null;
    }

    //endregion


}
