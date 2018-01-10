/*
 * Created by Administrator on 02/01/2018
 * Last modified 15:41 28/12/17
 */

package businessLogic.droneVideoProvider;

import java.util.Map;

/**
 * Created by Administrator on 28/12/2017.
 */

public interface FFmpegInterface
{
    /**
     * Executes a command
     *
     * @param environvenmentVars           Environment variables
     * @param cmd                          command to execute
     * @param ffmpegExecuteResponseHandler {@link FFmpegExecuteResponseHandler}
     * @throws FFmpegCommandAlreadyRunningException
     */
    public void execute(Map<String, String> environvenmentVars, String[] cmd, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) throws FFmpegCommandAlreadyRunningException;

    /**
     * Executes a command
     *
     * @param cmd                          command to execute
     * @param ffmpegExecuteResponseHandler {@link FFmpegExecuteResponseHandler}
     * @throws FFmpegCommandAlreadyRunningException
     */
    public void execute(String[] cmd, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) throws FFmpegCommandAlreadyRunningException;

}
