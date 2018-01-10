/*
 * Created by Administrator on 02/01/2018
 * Last modified 13:52 28/12/17
 */

package businessLogic.droneVideoProvider;

/**
 * Created by Administrator on 28/12/2017.
 */

public interface FFmpegExecuteResponseHandler extends ResponseHandler
{
    /**
     * on Success
     * @param message complete output of the FFmpegLocal command
     */
    public void onSuccess(String message);

    /**
     * on Progress
     * @param message current output of FFmpegLocal command
     */
    public void onProgress(String message);

    /**
     * on Failure
     * @param message complete output of the FFmpegLocal command
     */
    public void onFailure(String message);
}
