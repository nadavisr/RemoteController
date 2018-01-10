/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.serialization.interfaces;

import java.io.IOException;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.serialization.interfaces.</P>
 * <P>Defines the ability to deserialize any type data.</P>
 */

public interface IDeserializer {

    <TData> TData deserialize(byte[] dataBuffer, int dataStartIndex, int dataLength) throws IOException;

    <TData> TData deserialize(byte[] dataBuffer) throws IOException;
}

