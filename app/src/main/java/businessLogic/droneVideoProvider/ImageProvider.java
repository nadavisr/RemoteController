/*
 * Created by Administrator on 02/01/2018
 * Last modified 09:46 01/01/18
 */

package businessLogic.droneVideoProvider;

/**
 * Created by Administrator on 01/01/2018.
 */

public interface ImageProvider {
    void frameReady(byte[] rgb, int width, int hight);
}
