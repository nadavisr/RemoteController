/*
 * Created by Administrator on 02/01/2018
 * Last modified 13:52 28/12/17
 */

package businessLogic.droneVideoProvider;

/**
 * Created by Administrator on 28/12/2017.
 */

public interface FFmpegLoadBinaryResponseHandler extends ResponseHandler {

    /**
     * on Fail
     */
    public void onFailure();

    /**
     * on Success
     */
    public void onSuccess();

}
