/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.serialization;

import org.msgpack.MessagePack;

import java.io.IOException;

import businessLogic.serialization.interfaces.ITypedDeserializer;
import businessLogic.serialization.interfaces.ITypedSerializer;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.serialization.</P>
 * <P>calls which serialize and deserialize certain type data, using {@link MessagePack} protocol.</P>
 *
 * @param <TData> The type of serialized and deserialized data.
 * @see ITypedDeserializer
 * @see ITypedSerializer
 */

public class MsgPackSerializer<TData> implements ITypedDeserializer<TData>, ITypedSerializer<TData> {

    private final MessagePack m_messagePack;
    private final Class<TData> m_type;

    public MsgPackSerializer(Class<TData> type) {
        m_type = type;
        m_messagePack = new MessagePack();
    }


    @Override
    public TData deserialize(byte[] dataBuffer, int dataStartIndex, int dataLength) throws IOException {
        return m_messagePack.read(dataBuffer, dataStartIndex, dataLength, m_type);
    }

    @Override
    public TData deserialize(byte[] dataBuffer) throws IOException {
        return m_messagePack.read(dataBuffer, m_type);
    }

    @Override
    public byte[] serialize(TData data) throws IOException {
        return m_messagePack.write(data);
    }

    @Override
    public void serialize(TData data, byte[] dataBuffer, int startIndex) throws IOException {
        byte[] bytes = m_messagePack.write(data);
        int bufferSize = dataBuffer.length - startIndex;
        if (bufferSize < bytes.length) {
            String msg = "The buffer is not big enough to the serialized data. buffer empty size: " + bufferSize + " , serialized data size: " + bytes.length;
            throw new IOException(msg);
        }
        System.arraycopy(bytes, 0, dataBuffer, startIndex, bytes.length);
    }
}
