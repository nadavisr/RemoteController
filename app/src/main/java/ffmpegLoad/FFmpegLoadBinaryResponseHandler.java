/*
 * Created by Administrator on 09/01/2018
 * Last modified 13:52 28/12/17
 */

package ffmpegLoad;

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