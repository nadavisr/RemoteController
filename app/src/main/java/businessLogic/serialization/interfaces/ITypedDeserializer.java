/*
 * Created by admin on  27/09/2017
 * Last modified 19:17 26/09/17
 */

package businessLogic.serialization.interfaces;

import java.io.IOException;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.serialization.interfaces.</P>
 * <P>Defines the ability to deserialize certain type data.</P>
 * @param <TData> The type of deserialized data.
 */

public interface ITypedDeserializer<TData> {

    TData deserialize(byte[] dataBuffer, int dataStartIndex, int dataLength) throws IOException;

    TData deserialize(byte[] dataBuffer) throws IOException;
}
