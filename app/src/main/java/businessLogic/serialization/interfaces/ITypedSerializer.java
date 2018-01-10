/*
 * Created by admin on  27/09/2017
 * Last modified 19:17 26/09/17
 */

package businessLogic.serialization.interfaces;

import java.io.IOException;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.serialization.interfaces.</P>
 * <P>Defines the ability to serialize certain type data.</P>
 *
 * @param <TData> The type of serialized data.
 */

public interface ITypedSerializer<TData> {

    byte[] serialize(TData data) throws IOException;

    void serialize(TData data, byte[] dataBuffer, int startIndex) throws IOException;

}
