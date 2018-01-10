/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.serialization.interfaces;

import java.io.IOException;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.serialization.interfaces.</P>
 * <P>Defines the ability to serialize any type data.</P>
 */

public interface ISerializer {

    <TData> byte[] serialize(TData data) throws IOException;

    <TData> void serialize(TData data,byte buffer,int startIndex) throws IOException;

}

